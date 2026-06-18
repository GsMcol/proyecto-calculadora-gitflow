class HardwareCalculatorError extends Error {
  constructor(message) {
    super(message);
    this.name = 'HardwareCalculatorError';
  }
}

const hardwareCatalog = {
  cpus: [
    { name: 'Ryzen 9 9950X3D', score: 98, generation: 'Zen 5' },
    { name: 'Core i9-14900K', score: 96, generation: 'Raptor Lake' },
    { name: 'Ryzen 7 7800X3D', score: 88, generation: 'Zen 4' },
    { name: 'Core i5-14600K', score: 78, generation: 'Raptor Lake' },
    { name: 'Ryzen 5 7600', score: 72, generation: 'Zen 4' }
  ],
  gpus: [
    { name: 'RTX 5090', score: 100, tier: 'Ultra' },
    { name: 'RX 7900 XT', score: 92, tier: 'High' },
    { name: 'RTX 4080', score: 85, tier: 'High' },
    { name: 'RX 6600', score: 62, tier: 'Mid' },
    { name: 'GTX 1660 Super', score: 45, tier: 'Entry' }
  ],
  resolutions: {
    '1080p': 1.0,
    '1440p': 0.85,
    '4K': 0.6
  }
};

function findHardwareItem(items, name) {
  return items.find(item => item.name.toLowerCase() === name.toLowerCase()) || null;
}

function validateInputs(cpuName, gpuName, resolution) {
  if (!cpuName || !gpuName || !resolution) {
    throw new HardwareCalculatorError('CPU, GPU y resolución son obligatorios.');
  }

  const cpu = findHardwareItem(hardwareCatalog.cpus, cpuName);
  const gpu = findHardwareItem(hardwareCatalog.gpus, gpuName);
  const resolutionPenalty = hardwareCatalog.resolutions[resolution];

  if (!cpu) {
    throw new HardwareCalculatorError(`CPU no encontrada: ${cpuName}`);
  }

  if (!gpu) {
    throw new HardwareCalculatorError(`GPU no encontrada: ${gpuName}`);
  }

  if (resolutionPenalty == null) {
    throw new HardwareCalculatorError(`Resolución inválida: ${resolution}. Use 1080p, 1440p o 4K.`);
  }

  return { cpu, gpu, resolutionPenalty };
}

function estimatePerformance(cpu, gpu) {
  const baseScore = (cpu.score * 0.5) + (gpu.score * 0.5);
  const synergyBonus = cpu.score > 90 && gpu.score > 90 ? 5 : 0;
  return baseScore + synergyBonus;
}

function applyResolutionPenalty(basePerformance, penalty) {
  return Math.round(basePerformance * penalty);
}

function buildReport(cpu, gpu, resolution, performance) {
  const bottleneckComponent = cpu.score < gpu.score ? 'CPU' : cpu.score > gpu.score ? 'GPU' : 'Ninguno evidente';

  return {
    cpu: cpu.name,
    gpu: gpu.name,
    resolution,
    estimatedPerformance: performance,
    probableBottleneck: bottleneckComponent
  };
}

function calcularRendimiento(cpuName, gpuName, resolution) {
  try {
    const { cpu, gpu, resolutionPenalty } = validateInputs(cpuName, gpuName, resolution);
    const basePerformance = estimatePerformance(cpu, gpu);
    const finalPerformance = applyResolutionPenalty(basePerformance, resolutionPenalty);
    return buildReport(cpu, gpu, resolution, finalPerformance);
  } catch (error) {
    if (error instanceof HardwareCalculatorError) {
      throw error;
    }

    throw new HardwareCalculatorError('Error inesperado al calcular el rendimiento.');
  }
}
