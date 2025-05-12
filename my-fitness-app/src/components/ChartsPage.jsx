import { useRef, useEffect, useState } from "react";
import {
    Chart,
    BarController,
    BarElement,
    LineController,
    LineElement,
    PointElement,
    RadarController,
    CategoryScale,
    LinearScale,
    RadialLinearScale,
    Title,
    Tooltip,
    Legend,
    Filler,
} from 'chart.js';

Chart.register(
    BarController,
    BarElement,
    LineController,
    LineElement,
    PointElement,
    RadarController,
    CategoryScale,
    LinearScale,
    RadialLinearScale,
    Title,
    Tooltip,
    Legend,
    Filler
);


function ChartsPage() {
    const [timePeriod, setTimePeriod] = useState('week');  // 預設是 weekly
    const [dateRange, setDateRange] = useState('current'); // 預設是 current
    const [selectedExercise, setSelectedExercise] = useState('benchPress');

    const [height, setHeight] = useState('');
    const [weight, setWeight] = useState('');
    const [showResult, setShowResult] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // 新增 weight 資訊
    const [weightChangeText, setWeightChangeText] = useState('-1.2 kg');
    const [weightRateText, setWeightRateText] = useState('-0.3 kg/week');
    const [weightChangeColor, setWeightChangeColor] = useState('text-green-600');


    const [bmi, setBmi] = useState({
        value: '',
        category: '',
        indicator: 0,
        color: '',
    });
    
    const calculateBMI = () => {
        const heightNum = parseFloat(height);
        const weightNum = parseFloat(weight);
    
        if (isNaN(heightNum) || isNaN(weightNum) || heightNum <= 0 || weightNum <= 0) {
            alert('Please enter valid height and weight values');
            return;
        }
    
        const heightM = heightNum / 100;
        const bmiValue = (weightNum / (heightM * heightM)).toFixed(1);
    
        let category = '';
        let indicator = 0;
        let color = '';
    
        if (bmiValue < 18.5) {
            category = 'Underweight';
            indicator = (bmiValue / 18.5) * 25;
            color = 'text-blue-600';
        } else if (bmiValue < 25) {
            category = 'Normal weight';
            indicator = 25 + ((bmiValue - 18.5) / 6.5) * 25;
            color = 'text-green-600';
        } else if (bmiValue < 30) {
            category = 'Overweight';
            indicator = 50 + ((bmiValue - 25) / 5) * 25;
            color = 'text-yellow-600';
        } else {
            category = 'Obese';
            indicator = 75 + Math.min(((bmiValue - 30) / 10) * 25, 25);
            color = 'text-red-600';
        }
    
        setBmi({
            value: bmiValue,
            category,
            indicator,
            color,
        });
        setShowResult(true);
    };  

    //TDEE section
    const [tdeeForm, setTdeeForm] = useState({
        age: '',
        gender: 'male',
        height: '',
        weight: '',
        activity: '1.2',
    });
    
    const [tdeeResult, setTdeeResult] = useState({
        tdee: '',
        loss: '',
        maintain: '',
        gain: '',
    });
    
    const [showTdeeResult, setShowTdeeResult] = useState(false);

    const calculateTDEE = () => {
        const age = parseInt(tdeeForm.age);
        const gender = tdeeForm.gender;
        const height = parseFloat(tdeeForm.height);
        const weight = parseFloat(tdeeForm.weight);
        const activityLevel = parseFloat(tdeeForm.activity);
    
        if (isNaN(age) || isNaN(height) || isNaN(weight) || age <= 0 || height <= 0 || weight <= 0) {
            alert('Please enter valid values for all fields');
            return;
        }
    
        // Mifflin-St Jeor Equation
        let bmr;
        if (gender === 'male') {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
    
        const tdee = Math.round(bmr * activityLevel);
    
        setTdeeResult({
            tdee: tdee.toLocaleString(),
            maintain: tdee.toLocaleString(),
            loss: Math.round(tdee * 0.8).toLocaleString(),
            gain: Math.round(tdee * 1.2).toLocaleString(),
        });
        setShowTdeeResult(true);
    };

    // Ref 定義
    const tagFrequencyRef = useRef(null);
    const muscleGroupRef = useRef(null);
    const volumeProgressionRef = useRef(null);
    const weightTrendRef = useRef(null);

    // Chart 實例
    let tagFrequencyChart;
    let muscleGroupChart;
    let volumeProgressionChart;
    let weightTrendChart;

    useEffect(() => {
        // Tag frequency chart
        const tagCtx = tagFrequencyRef.current.getContext('2d');
        tagFrequencyChart = new Chart(tagCtx, {
            type: 'bar',
            data: {
                labels: chartData.tags.weekly.labels,
                datasets: [{
                    label: 'Frequency',
                    data: chartData.tags.weekly.data,
                    backgroundColor: '#1a1a1a',
                    borderColor: '#1a1a1a',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Count'
                        }
                    }
                }
            }
        });
    
        // Muscle group chart
        const muscleCtx = muscleGroupRef.current.getContext('2d');
        muscleGroupChart = new Chart(muscleCtx, {
            type: 'radar',
            data: {
                labels: chartData.muscleGroups.weekly.labels,
                datasets: [{
                    label: 'Sets',
                    data: chartData.muscleGroups.weekly.data,
                    backgroundColor: 'rgba(26, 26, 26, 0.2)',
                    borderColor: '#1a1a1a',
                    borderWidth: 2,
                    pointBackgroundColor: '#1a1a1a'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        beginAtZero: true
                    }
                }
            }
        });
    
        // Volume progression chart
        const volumeCtx = volumeProgressionRef.current.getContext('2d');
        volumeProgressionChart = new Chart(volumeCtx, {
            type: 'line',
            data: {
                labels: chartData.exerciseVolume.benchPress.weekly.labels,
                datasets: [{
                    label: 'Volume (reps × weight)',
                    data: chartData.exerciseVolume.benchPress.weekly.data,
                    backgroundColor: 'rgba(26, 26, 26, 0.1)',
                    borderColor: '#1a1a1a',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: false,
                        title: {
                            display: true,
                            text: 'Total Volume'
                        }
                    }
                }
            }
        });
    
        // Weight trend chart
        const weightCtx = weightTrendRef.current.getContext('2d');
        weightTrendChart = new Chart(weightCtx, {
            type: 'line',
            data: {
                labels: chartData.weightTrend.weekly.labels,
                datasets: [{
                    label: 'Weight (kg)',
                    data: chartData.weightTrend.weekly.data,
                    backgroundColor: 'rgba(52, 211, 153, 0.1)',
                    borderColor: '#34D399',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: false,
                        title: {
                            display: true,
                            text: 'Weight (kg)'
                        }
                    }
                }
            }
        });
    
        // ✅ 清理圖表 (避免重複渲染時報錯)
        return () => {
            tagFrequencyChart.destroy();
            muscleGroupChart.destroy();
            volumeProgressionChart.destroy();
            weightTrendChart.destroy();
        };
    
    }, []);
    
        // Sample data for charts
        const chartData = {
            // Tag frequency data
            tags: {
                weekly: {
                    labels: ['Strength', 'Cardio', 'Upper Body', 'Lower Body', 'Core', 'Flexibility'],
                    data: [4, 2, 3, 2, 1, 1]
                },
                twoWeeks: {
                    labels: ['Strength', 'Cardio', 'Upper Body', 'Lower Body', 'Core', 'Flexibility'],
                    data: [8, 4, 6, 5, 3, 2]
                },
                monthly: {
                    labels: ['Strength', 'Cardio', 'Upper Body', 'Lower Body', 'Core', 'Flexibility'],
                    data: [16, 8, 12, 10, 6, 4]
                }
            },
            
            // Muscle group data
            muscleGroups: {
                weekly: {
                    labels: ['Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core'],
                    data: [12, 10, 15, 8, 9, 6]
                },
                twoWeeks: {
                    labels: ['Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core'],
                    data: [24, 22, 30, 18, 20, 14]
                },
                monthly: {
                    labels: ['Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core'],
                    data: [48, 45, 60, 36, 42, 30]
                }
            },
            
            // Exercise volume data
            exerciseVolume: {
                benchPress: {
                    weekly: {
                        labels: ['Mon', 'Wed', 'Fri'],
                        data: [1600, 1650, 1700]
                    },
                    twoWeeks: {
                        labels: ['Week 1-Mon', 'Week 1-Wed', 'Week 1-Fri', 'Week 2-Mon', 'Week 2-Wed', 'Week 2-Fri'],
                        data: [1550, 1600, 1650, 1600, 1650, 1700]
                    },
                    monthly: {
                        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                        data: [1550, 1650, 1700, 1750]
                    }
                },
                squat: {
                    weekly: {
                        labels: ['Tue', 'Thu', 'Sat'],
                        data: [2400, 2450, 2500]
                    },
                    twoWeeks: {
                        labels: ['Week 1-Tue', 'Week 1-Thu', 'Week 1-Sat', 'Week 2-Tue', 'Week 2-Thu', 'Week 2-Sat'],
                        data: [2350, 2400, 2450, 2400, 2450, 2500]
                    },
                    monthly: {
                        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                        data: [2350, 2450, 2500, 2550]
                    }
                },
                deadlift: {
                    weekly: {
                        labels: ['Mon', 'Fri'],
                        data: [3000, 3050]
                    },
                    twoWeeks: {
                        labels: ['Week 1-Mon', 'Week 1-Fri', 'Week 2-Mon', 'Week 2-Fri'],
                        data: [2950, 3000, 3000, 3050]
                    },
                    monthly: {
                        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                        data: [2950, 3000, 3050, 3100]
                    }
                },
                shoulderPress: {
                    weekly: {
                        labels: ['Wed', 'Sat'],
                        data: [1200, 1250]
                    },
                    twoWeeks: {
                        labels: ['Week 1-Wed', 'Week 1-Sat', 'Week 2-Wed', 'Week 2-Sat'],
                        data: [1150, 1200, 1200, 1250]
                    },
                    monthly: {
                        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                        data: [1150, 1200, 1250, 1300]
                    }
                },
                pullUp: {
                    weekly: {
                        labels: ['Tue', 'Thu', 'Sun'],
                        data: [800, 850, 900]
                    },
                    twoWeeks: {
                        labels: ['Week 1-Tue', 'Week 1-Thu', 'Week 1-Sun', 'Week 2-Tue', 'Week 2-Thu', 'Week 2-Sun'],
                        data: [750, 800, 800, 800, 850, 900]
                    },
                    monthly: {
                        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                        data: [750, 800, 850, 900]
                    }
                }
            },
            
            // Weight trend data
            weightTrend: {
                weekly: {
                    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                    data: [78.5, 78.3, 78.4, 78.2, 78.0, 77.8, 77.7]
                },
                twoWeeks: {
                    labels: ['Week 1-Mon', 'Week 1-Wed', 'Week 1-Fri', 'Week 1-Sun', 'Week 2-Tue', 'Week 2-Thu', 'Week 2-Sat'],
                    data: [79.0, 78.8, 78.6, 78.5, 78.2, 78.0, 77.7]
                },
                monthly: {
                    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                    data: [79.5, 79.0, 78.5, 77.7]
                }
            }
        };

        // Update all charts based on time period
        function updateAllCharts() {
            updateTagFrequencyChart();
            updateMuscleGroupChart();
            updateVolumeChart();
            updateWeightTrendChart();
        }

        // Update tag frequency chart
        function updateTagFrequencyChart() {
            const data = chartData.tags[timePeriod];
            tagFrequencyChart.data.labels = data.labels;
            tagFrequencyChart.data.datasets[0].data = data.data;
            tagFrequencyChart.update();
        }
                                
        // Update muscle group chart
        function updateMuscleGroupChart() {
            const data = chartData.muscleGroups[timePeriod];
            muscleGroupChart.data.labels = data.labels;
            muscleGroupChart.data.datasets[0].data = data.data;
            muscleGroupChart.update();
        }

    const updateVolumeChart = () => {
        const data = chartData.exerciseVolume[selectedExercise][timePeriod];
        volumeProgressionChart.data.labels = data.labels;
        volumeProgressionChart.data.datasets[0].data = data.data;
        volumeProgressionChart.update();
    };
        

        // Update weight trend chart
        function updateWeightTrendChart() {
            const data = chartData.weightTrend[timePeriod];
            weightTrendChart.data.labels = data.labels;
            weightTrendChart.data.datasets[0].data = data.data;
            weightTrendChart.update();
            
            // Update weight change info
            const weightData = data.data;
            const firstWeight = weightData[0];
            const lastWeight = weightData[weightData.length - 1];
            const weightChange = lastWeight - firstWeight;
            const weightChangeAbs = Math.abs(weightChange).toFixed(1);
            const weightChangeDirection = weightChange < 0 ? 'loss' : 'gain';
            const weightChangeColor = weightChange < 0 ? 'text-green-600' : 'text-red-600';

            
            // Calculate rate of change
            let rateText = '';
            if (timePeriod === 'week') {
                rateText = `${weightChange < 0 ? '-' : '+'}${weightChangeAbs} kg/week`;
            } else if (timePeriod === 'twoWeeks') {
                const weeklyRate = (weightChange / 2).toFixed(1);
                rateText = `${weightChange < 0 ? '-' : '+'}${Math.abs(weeklyRate)} kg/week`;
            } else {
                const weeklyRate = (weightChange / 4).toFixed(1);
                rateText = `${weightChange < 0 ? '-' : '+'}${Math.abs(weeklyRate)} kg/week`;
            }

            // 更新 React 狀態
            setWeightChangeText(`${weightChange < 0 ? '-' : '+'}${weightChangeAbs} kg`);
            setWeightRateText(rateText);
            setWeightChangeColor(weightChangeColor);
        }


    return (
    <div>
        <div className="flex min-h-screen">
            {/* <!-- Main Content --> */}
            <div className="flex-1">
                {/* <!-- Content Area --> */}
                <div className="p-4 md:p-6">
                    <div className="max-w-6xl mx-auto">
                        {/* <!-- Header --> */}
                        <div className="mb-6">
                            <h1 className="text-2xl font-bold text-gray-900">Fitness Analytics</h1>
                            <p className="text-gray-600">Track your progress and analyze your training data</p>
                        </div>

                        {/* <!-- Time Period Filter --> */}
                        <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                            <div className="flex flex-col md:flex-row gap-3">
                                <div className="flex-1">
                                    <label className="block text-gray-700 mb-2">Select Time Period</label>
                                    <div className="flex gap-2">
                                        <button className={`time-filter-btn px-4 py-2 rounded-lg ${
                                                    timePeriod === 'month' ? 'bg-gray-700 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                                }`}
                                                onClick={() => {
                                                    setTimePeriod('month');
                                                    updateAllCharts();  // 注意：這要自己定義 function
                                                }}
                                                // className="time-filter-btn px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300" data-period="month"
                                        >
                                            Monthly</button>
                                        <button className={`time-filter-btn px-4 py-2 rounded-lg ${
                                                    timePeriod === 'twoWeeks' ? 'bg-gray-700 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                                }`}
                                                onClick={() => {
                                                    setTimePeriod('twoWeeks');
                                                    updateAllCharts();  // 注意：這要自己定義 function
                                                }}
                                        // className="time-filter-btn px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300" data-period="twoWeeks"
                                        >Two Weeks</button>
                                        <button className={`time-filter-btn px-4 py-2 rounded-lg ${
                                                    timePeriod === 'week' ? 'bg-gray-700 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                                }`}
                                                onClick={() => {
                                                    setTimePeriod('week');
                                                    updateAllCharts();  // 注意：這要自己定義 function
                                                }}
                                        // className="time-filter-btn active px-4 py-2 rounded-lg" data-period="week"
                                        >Weekly</button>
                                    </div>
                                </div>
                                <div className="flex items-end">
                                    <select
                                        value={dateRange}
                                        onChange={(e) => {
                                            setDateRange(e.target.value);
                                            updateAllCharts();  // 更新時傳目前的狀態
                                        }}
                                        className="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                        id="date-selector"
                                    >
                                        <option value="current">Current Week</option>
                                        <option value="previous">Previous Week</option>
                                        <option value="twoWeeksAgo">Two Weeks Ago</option>
                                        <option value="threeWeeksAgo">Three Weeks Ago</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        {/* <!-- Charts Grid --> */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            {/* <!-- Tag Frequency Chart --> */}
                            <div className="bg-white rounded-lg shadow-sm p-4">
                                <h2 className="text-lg font-semibold mb-4">Workout Tag Frequency</h2>
                                <div className="chart-container">
                                    <canvas ref={tagFrequencyRef} id="tagFrequencyChart"></canvas>
                                </div>
                            </div>

                            {/* <!-- Muscle Group Sets Chart --> */}
                            <div className="bg-white rounded-lg shadow-sm p-4">
                                <h2 className="text-lg font-semibold mb-4">Muscle Group Training Balance</h2>
                                <div className="chart-container">
                                    <canvas ref={muscleGroupRef} id="muscleGroupChart"></canvas>
                                </div>
                            </div>

                            {/* <!-- Exercise Volume Chart --> */}
                            <div className="bg-white rounded-lg shadow-sm p-4">
                                <h2 className="text-lg font-semibold mb-4">Exercise Volume Progression</h2>
                                <div className="mb-3">
                                    <label className="block text-gray-700 mb-2">Select Exercise</label>
                                    <select
                                        value={selectedExercise}
                                        onChange={(e) => setSelectedExercise(e.target.value)}
                                        id="exercise-selector"
                                        className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                    >
                                        <option value="benchPress">Bench Press</option>
                                        <option value="squat">Squat</option>
                                        <option value="deadlift">Deadlift</option>
                                        <option value="shoulderPress">Shoulder Press</option>
                                        <option value="pullUp">Pull Up</option>
                                    </select>
                                </div>
                                <div className="chart-container">
                                    <canvas ref={volumeProgressionRef} id="volumeProgressionChart"></canvas>
                                </div>
                            </div>

                            {/* <!-- Weight Trend Chart --> */}
                            <div className="bg-white rounded-lg shadow-sm p-4">
                                <h2 className="text-lg font-semibold mb-4">Body Weight Trend</h2>
                                <div className="chart-container">
                                    <canvas ref={weightTrendRef} id="weightTrendChart"></canvas>
                                </div>
                                <div className="mt-3 p-3 bg-gray-50 rounded-lg">
                                    <div className="flex justify-between items-center">
                                        <div>
                                            <span className="text-gray-600">Change:</span>
                                            <span id="weight-change" className={`font-semibold text-green-600 ${weightChangeColor}`}>
                                                {weightChangeText}
                                            </span>
                                        </div>
                                        <div>
                                            <span className="text-gray-600">Rate:</span>
                                            <span id="weight-rate" className={`font-semibold text-green-600 ${weightChangeColor}`}>
                                                {weightRateText}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* <!-- Calculators Section --> */}
                        <div className="mb-6">
                            <h2 className="text-xl font-bold text-gray-900 mb-4">Fitness Calculators</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                {/* <!-- BMI Calculator --> */}
                                <div className="calculator-card bg-white rounded-lg shadow-sm p-6">
                                    <h3 className="text-lg font-semibold mb-4">BMI Calculator</h3>
                                    <div className="space-y-4">
                                        <div>
                                            <label className="block text-gray-700 mb-2">Height (cm)</label>
                                            <input
                                                type="number"
                                                value={height}
                                                onChange={(e) => setHeight(e.target.value)}
                                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                placeholder="Enter your height"
                                                id="bmi-height"
                                            />
                                        </div>
                                        <div>
                                            <label className="block text-gray-700 mb-2">Weight (kg)</label>
                                            <input
                                                type="number"
                                                value={weight}
                                                onChange={(e) => setWeight(e.target.value)}
                                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                placeholder="Enter your weight"
                                                id="bmi-weight"
                                            />
                                        </div>
                                        <button
                                            className="w-full bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition"
                                            onClick={calculateBMI}
                                            id="calculate-bmi"
                                        >
                                            Calculate BMI
                                        </button>
                                        {showResult && (
                                            <div id="bmi-result" className="mt-4">
                                                <div className="p-4 bg-gray-50 rounded-lg">
                                                    <div className="flex justify-between items-center mb-2">
                                                        <span className="text-gray-700">Your BMI:</span>
                                                        <span id="bmi-value" className="font-bold text-lg">{bmi.value}</span>
                                                    </div>
                                                    <div className="flex justify-between items-center">
                                                        <span className="text-gray-700">Category:</span>
                                                        <span
                                                            className={`font-medium cursor-pointer ${bmi.color}`}
                                                            onClick={() => setIsModalOpen(true)}
                                                            id="bmi-category"
                                                        >
                                                            {bmi.category}
                                                        </span>
                                                        {/* <span  className="font-medium">Normal weight</span> */}
                                                    </div>
                                                    <div className="mt-3">
                                                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                                                            <div    
                                                                className="bg-green-600 h-2.5 rounded-full"
                                                                id="bmi-indicator"
                                                                style={{
                                                                    width: `${bmi.indicator}%`,
                                                                    backgroundColor:
                                                                        bmi.color.includes('blue')
                                                                            ? '#3B82F6'
                                                                            : bmi.color.includes('green')
                                                                            ? '#10B981'
                                                                            : bmi.color.includes('yellow')
                                                                            ? '#F59E0B'
                                                                            : '#EF4444', // red
                                                                }}
                                                            ></div>
                                                        </div>
                                                        <div className="flex justify-between text-xs mt-1 text-gray-500">
                                                            <span>Underweight</span>
                                                            <span>Normal</span>
                                                            <span>Overweight</span>
                                                            <span>Obese</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>

                                {/* <!-- TDEE Calculator --> */}
                                <div className="calculator-card bg-white rounded-lg shadow-sm p-6">
                                    <h3 className="text-lg font-semibold mb-4">TDEE Calculator</h3>
                                    <div className="space-y-4">
                                        <div className="grid grid-cols-2 gap-4">
                                            <div>
                                                <label className="block text-gray-700 mb-2">Age</label>
                                                <input
                                                    id="tdee-age"
                                                    type="number"
                                                    className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                    placeholder="Years"
                                                    value={tdeeForm.age}
                                                    onChange={(e) => setTdeeForm({ ...tdeeForm, age: e.target.value })}
                                                />
                                            </div>
                                            <div>
                                                <label className="block text-gray-700 mb-2">Gender</label>
                                                <select
                                                    id="tdee-gender"
                                                    className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                    value={tdeeForm.gender}
                                                    onChange={(e) => setTdeeForm({ ...tdeeForm, gender: e.target.value })}
                                                >
                                                    <option value="male">Male</option>
                                                    <option value="female">Female</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div className="grid grid-cols-2 gap-4"> {/* mt-4*/} 
                                            <div>
                                                <label className="block text-gray-700 mb-2">Height (cm)</label>
                                                <input
                                                    type="number"
                                                    id="tdee-height"
                                                    className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                    placeholder="cm"
                                                    value={tdeeForm.height}
                                                    onChange={(e) => setTdeeForm({ ...tdeeForm, height: e.target.value })}
                                                />
                                            </div>
                                            <div>
                                                <label className="block text-gray-700 mb-2">Weight (kg)</label>
                                                <input
                                                    id="tdee-weight"
                                                    type="number"
                                                    className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                    placeholder="kg"
                                                    value={tdeeForm.weight}
                                                    onChange={(e) => setTdeeForm({ ...tdeeForm, weight: e.target.value })}
                                                />
                                            </div>
                                        </div>
                                        <div> {/* mt-4*/} 
                                            <label className="block text-gray-700 mb-2">Activity Level</label>
                                            <select
                                                id="tdee-activity"
                                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                                value={tdeeForm.activity}
                                                onChange={(e) => setTdeeForm({ ...tdeeForm, activity: e.target.value })}
                                            >
                                                <option value="1.2">Sedentary (office job)</option>
                                                <option value="1.375">Light Exercise (1-2 days/week)</option>
                                                <option value="1.55">Moderate Exercise (3-5 days/week)</option>
                                                <option value="1.725">Heavy Exercise (6-7 days/week)</option>
                                                <option value="1.9">Athlete (2x per day)</option>
                                            </select>
                                        </div>
                                        <button
                                            id="calculate-tdee"
                                            className="w-full bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition" //mt-4
                                            onClick={calculateTDEE}
                                        >
                                            Calculate TDEE
                                        </button>
                                            
                                        {showTdeeResult && (
                                            <div id="tdee-result" className="mt-4">
                                                <div className="p-4 bg-gray-50 rounded-lg">
                                                    <div className="mb-3">
                                                        <div className="text-center mb-1 text-gray-700">Your Daily Calories</div>
                                                        <div className="text-center font-bold text-2xl text-gray-900" id="tdee-value">{tdeeResult.tdee}</div>
                                                    </div>
                                                    <div className="grid grid-cols-3 gap-2 text-center text-sm">
                                                        <div className="p-2 bg-white rounded-lg">
                                                            <div className="text-gray-600">Weight Loss</div>
                                                            <div className="font-semibold" id="tdee-loss">{tdeeResult.loss}</div>
                                                        </div>
                                                        <div className="p-2 bg-white rounded-lg">
                                                            <div className="text-gray-600">Maintenance</div>
                                                            <div className="font-semibold" id="tdee-maintain">{tdeeResult.maintain}</div>
                                                        </div>
                                                        <div className="p-2 bg-white rounded-lg">
                                                            <div className="text-gray-600">Weight Gain</div>
                                                            <div className="font-semibold" id="tdee-gain">{tdeeResult.gain}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        {/* <!-- BMI Info Modal --> */}
        {isModalOpen && (
            <div
                id="bmi-info-modal"
                className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
                onClick={() => setIsModalOpen(false)}
            >
                <div
                    className="bg-white rounded-lg p-6 w-full max-w-md"
                    onClick={(e) => e.stopPropagation()}
                >
                    <h3 className="text-xl font-bold mb-4">BMI Categories</h3>
                    <div className="space-y-3">
                        {[
                            { label: 'Underweight', range: 'Below 18.5' },
                            { label: 'Normal weight', range: '18.5 - 24.9' },
                            { label: 'Overweight', range: '25 - 29.9' },
                            { label: 'Obesity (Class 1)', range: '30 - 34.9' },
                            { label: 'Obesity (Class 2)', range: '35 - 39.9' },
                            { label: 'Extreme Obesity', range: '40 or higher' },
                        ].map((item, idx) => (
                            <div className="flex justify-between" key={idx}>
                                <span>{item.label}</span>
                                <span className="font-medium">{item.range}</span>
                            </div>
                        ))}
                    </div>
                    <div className="mt-6">
                        <p className="text-gray-600 text-sm">
                            BMI is a screening tool, but it does not diagnose body fatness or health. A healthcare provider can help you interpret your BMI results.
                        </p>
                    </div>
                    <div className="flex justify-end mt-6">
                        <button
                            id="close-bmi-info"
                            onClick={() => setIsModalOpen(false)}
                            className="px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition"
                        >
                            Close
                        </button>
                    </div>
                </div>
            </div>
        )}
    </div>
    );
}

export default ChartsPage;