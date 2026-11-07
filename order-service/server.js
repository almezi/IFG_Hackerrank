const express = require('express');
const { Kafka } = require('kafkajs');

const PORT = process.env.PORT || 8080;
const KAFKA_BROKERS = (process.env.KAFKA_BROKERS || 'localhost:9092').split(',');
const ORDER_TOPIC = process.env.ORDER_TOPIC || 'ifg-topic';

const app = express();
app.use(express.json());

// In-memory order store, keyed by order_id
const orders = new Map();

const kafka = new Kafka({ clientId: 'order-service', brokers: KAFKA_BROKERS });
const producer = kafka.producer();

async function connectKafka() {
  try {
    await producer.connect();
    console.log(`Connected to Kafka at ${KAFKA_BROKERS.join(',')}`);
  } catch (err) {
    console.error(`Kafka connection failed (${err.message}) — retrying in 5s. API stays available.`);
    setTimeout(connectKafka, 5000);
  }
}

app.post('/api/create', async (req, res) => {
  const order = req.body;
  if (!order || !order.order_id) {
    return res.status(400).json({ error: 'order_id is required' });
  }

  orders.set(order.order_id, order);

  try {
    await producer.send({
      topic: ORDER_TOPIC,
      messages: [{ key: order.order_id, value: JSON.stringify(order) }],
    });
    console.log(`Published order ${order.order_id} to topic '${ORDER_TOPIC}'`);
  } catch (err) {
    // Don't fail the API if Kafka is down; the event just isn't published.
    console.error(`Failed to publish order ${order.order_id}: ${err.message}`);
  }

  res.status(200).json(order);
});

app.get('/api/get-order', (req, res) => {
  const order = orders.get(req.query.id);
  if (!order) {
    return res.status(404).json({ error: `Order ${req.query.id} not found` });
  }
  res.status(200).json(order);
});

app.get('/health', (req, res) => {
  res.json({ status: 'UP', orders: orders.size });
});

app.listen(PORT, () => {
  console.log(`Order service listening on http://localhost:${PORT}`);
  console.log(`POST /api/create, GET /api/get-order?id=..., GET /health`);
  connectKafka();
});
