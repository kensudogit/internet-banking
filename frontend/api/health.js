module.exports = async function handler(req, res) {
  if (req.method !== 'GET') {
    return res.status(405).json({ message: 'Method not allowed' });
  }

  try {
    return res.status(200).json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'internet-banking-api',
      version: '1.0.0'
    });
  } catch (error) {
    console.error('Health check error:', error);
    return res.status(500).json({ 
      status: 'unhealthy',
      error: error.message 
    });
  }
}
