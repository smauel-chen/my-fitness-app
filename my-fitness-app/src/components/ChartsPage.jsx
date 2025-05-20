import { useRef, useEffect, useState } from "react";
import axiosInstance from '../api/axiosInstance.js';

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


function ChartsPage({userId}) {
    const [timePeriod, setTimePeriod] = useState('week');  // 預設是 weekly
    const [dateRange, setDateRange] = useState('current'); // 預設是 current
    
    const [height, setHeight] = useState('');
    const [weight, setWeight] = useState('');
    const [showResult, setShowResult] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

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

    // tag統計圖
    const tagFrequencyRef = useRef(null);
    const tagFrequencyChartRef = useRef(null);
    
    //雷達圖 肌群平衡
    const muscleGroupRef = useRef(null);
    const muscleGroupChartRef = useRef(null);

    // 動作進步圖
    const volumeProgressionRef = useRef(null);
    const volumeChartRef = useRef(null);
    const [selectedExerciseTypeId, setSelectedExerciseTypeId] = useState(null);
    const [volumeRecordCount, setVolumeRecordCount] = useState(5); // 預設查詢 5 筆紀錄
    const [exerciseOptions, setExerciseOptions] = useState([]);

    // 取得所有可選動作列表
    useEffect(() => {
        axiosInstance
        .get("/workout-types")
        .then((res) => {
            setExerciseOptions(res.data);
        })
        .catch((err) => console.error("取得動作選項失敗", err));
    }, []);

    //體重變化圖
    const weightTrendRef = useRef(null);
    const weightTrendChartRef = useRef(null);
    const [weightChangeText, setWeightChangeText] = useState('');
    const [weightRateText, setWeightRateText] = useState('');
    const [weightChangeColor, setWeightChangeColor] = useState('text-green-600');


    useEffect(() => {
        // Tag frequency chart
        if (!tagFrequencyRef.current) return;
        if (!muscleGroupRef.current) return;
        if (!volumeProgressionRef.current) return;
        if (!weightTrendRef.current) return;

        const tagFrequencyCtx = tagFrequencyRef.current.getContext('2d');
        if(tagFrequencyChartRef.current){
            tagFrequencyChartRef.current.destroy();
        }
        tagFrequencyChartRef.current = new Chart(tagFrequencyCtx, {
            type: 'bar',
            data: {
                    labels: [],
                    datasets: [{
                    label: 'Frequency',
                    data: [],
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
        const muscleGroupCtx = muscleGroupRef.current.getContext("2d");
        if (muscleGroupChartRef.current) {
            muscleGroupChartRef.current.destroy();
        }
        muscleGroupChartRef.current = new Chart(muscleGroupCtx, {
            type: "radar",
            data: {
                labels: [],
                datasets: [{
                    label: 'Sets',
                    data: [],
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
                        beginAtZero: true,
                        pointLabels: {//new
                            font: {
                              size: 12
                            }
                        }
                    }
                }
            }
        });
    
        // Volume progression chart
        const volumeProgressCtx = volumeProgressionRef.current.getContext("2d");
        if (volumeChartRef.current) {
            volumeChartRef.current.destroy();
        }
        volumeChartRef.current = new Chart(volumeProgressCtx, {
          type: 'line',
          data: {
            labels: [],
            datasets: [{
              label: 'Volume (reps × weight)',
              data: [],
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
        const weightTrendCtx = weightTrendRef.current.getContext("2d");
        if (weightTrendChartRef.current) {
            weightTrendChartRef.current.destroy();
        }   
        weightTrendChartRef.current = new Chart(weightTrendCtx, {
            type: "line",
            data: {
                labels: [],
                datasets: [{
                    label: "Weight (kg)",
                    data: [],
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

        return () => {
            if (tagFrequencyChartRef.current) {
                tagFrequencyChartRef.current.destroy();
            }
            if (muscleGroupChartRef.current) {
                muscleGroupChartRef.current.destroy();
            }
            if (volumeChartRef.current) {
                volumeChartRef.current.destroy();
            }
            if (weightTrendChartRef.current) {
                weightTrendChartRef.current.destroy();
            }
        };
    
    }, []);

    // submit new weight record
    const [inputDate, setInputDate] = useState(() => new Date().toISOString().split("T")[0]);
    const [inputWeight, setInputWeight] = useState('');
    const [inputMessage, setInputMessage] = useState('');

    const submitTodayWeight = async (date, weight) => {
        try {
            await axiosInstance.post(`/user/${userId}/weight`, { date, weight });
            await updateWeightTrendChart(); // 送出後立即更新圖表
        } catch (err) {
            alert('提交體重失敗');
        }
    };

    const periodMap = {
        week: 'weekly',
        twoWeeks: 'two-weeks',
        month: 'monthly'
    };
    
    //更新圖表當不同選項被觸發
    useEffect(() => {
        updateAllCharts();
        // if (muscleGroupRef.current) {
        //     updateMuscleGroupChart();
        // }
    }, [timePeriod, dateRange, userId]);

    const updateAllCharts = async () => {
        await updateTagFrequencyChart();
        await updateMuscleGroupChart();
        await updateWeightTrendChart();
    };

    useEffect(() => {
        if (volumeProgressionRef.current && selectedExerciseTypeId) {
            updateVolumeChart();
        }
    }, [selectedExerciseTypeId, volumeRecordCount, userId]); 

    //更新圖表方法     
    const updateTagFrequencyChart = async () => {
        try {
            const res = await axiosInstance.get(`/user/${userId}/charts/tag-frequency?period=${periodMap[timePeriod]}`);
            const data = res.data;
            const labels = Object.keys(data);
            const values = Object.values(data);
        
            // 更新圖表資料
            if(tagFrequencyChartRef.current){
                tagFrequencyChartRef.current.data.labels = labels;
                tagFrequencyChartRef.current.data.datasets[0].data = values;
                tagFrequencyChartRef.current.update();
            }
        } catch (err) {
            alert('讀取 Tag 頻率資料失敗：' + (err.response?.data?.message || err.message));
        }
    };
                      
    const updateMuscleGroupChart = async () => {
        try {
            const res = await axiosInstance.get(`/user/${userId}/charts/muscle-balance`, {
            params: { period: periodMap[timePeriod] }
        });
      
            const data = res.data;
            const labels = Object.keys(data);
            const values = Object.values(data);
        
            if (muscleGroupChartRef.current) {
                muscleGroupChartRef.current.data.labels = labels;
                muscleGroupChartRef.current.data.datasets[0].data = values;
                muscleGroupChartRef.current.update();
            }
        } catch (err) {
          console.error("肌群雷達圖資料載入失敗", err);
        }
    };

    const updateVolumeChart = async () => {
        if(!selectedExerciseTypeId) return;
        try {
            const res = await axiosInstance.get(`/user/${userId}/charts/volume-progress`, {
                params: {
                typeId: selectedExerciseTypeId,
                count: volumeRecordCount
                }
            });
        
            const data = res.data;
            const labels = data.map(entry => entry.date);
            const volumes = data.map(entry => entry.volume);
        
            if (volumeChartRef.current) {
              volumeChartRef.current.data.labels = labels;
              volumeChartRef.current.data.datasets[0].data = volumes;
              volumeChartRef.current.update();
            }
        } catch (err) {
          console.error("取得 Volume 圖表資料失敗", err);
        }
    };
      
    const updateWeightTrendChart = async () => {
        try {
            const period = periodMap[timePeriod];
            const res = await axiosInstance.get(`/user/${userId}/charts/weight-trend`, {
                params: { period }  // weekly, two-weeks, monthly
            });
            const data = res.data;
            const labels = data.map(d => d.date);
            const weights = data.map(d => d.weight);
    
            if (weightTrendChartRef.current) {
                weightTrendChartRef.current.data.labels = labels;
                weightTrendChartRef.current.data.datasets[0].data = weights;
                weightTrendChartRef.current.update();
            }
            
            // 更新加減體重狀態文字
            if (weights.length > 1) {
                
                const change = weights[weights.length - 1] - weights[0];
                const perWeek = period === 'two-weeks'
                    ? change / 2
                    : period === 'monthly'
                    ? change / 4
                    : change;
    
                setWeightChangeText(`${change >= 0 ? '+' : ''}${change.toFixed(1)} kg`);
                setWeightRateText(`${perWeek >= 0 ? '+' : ''}${perWeek.toFixed(1)} kg/week`);
                setWeightChangeColor(change < 0 ? 'text-green-600' : 'text-red-600');
            } else {
                setWeightChangeText('—');
                setWeightRateText('—');
                setWeightChangeColor('text-gray-500');
            }
            
        } catch (err) {
            console.error("體重圖表更新失敗", err);
        }
    };

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
                                                }
                                                
                                            }

                                        >
                                            Monthly</button>
                                        <button className={`time-filter-btn px-4 py-2 rounded-lg ${
                                                    timePeriod === 'twoWeeks' ? 'bg-gray-700 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                                }`}
                                                onClick={() => {
                                                    setTimePeriod('twoWeeks');
                                                }}
                                        >Two Weeks</button>
                                        <button className={`time-filter-btn px-4 py-2 rounded-lg ${
                                                    timePeriod === 'week' ? 'bg-gray-700 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                                }`}
                                                onClick={() => {
                                                    setTimePeriod('week');
                                                }}
                                        >Weekly</button>
                                    </div>
                                </div>
                                <div className="flex items-end">
                                    <select
                                        value={dateRange}
                                        onChange={(e) => {
                                            setDateRange(e.target.value);
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
                                <div className="mb-3 grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-gray-700 mb-1">Select Exercise</label>
                                        <select
                                            value={selectedExerciseTypeId || ''}
                                            onChange={(e) => {setSelectedExerciseTypeId(e.target.value)}}
                                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                        >
                                            <option value="">
                                                請選擇動作
                                            </option>
                                            {exerciseOptions.map((opt) => (
                                            <option key={opt.id} value={opt.id}>{opt.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                    <label className="block text-gray-700 mb-1">Record Count</label>
                                    <select
                                        value={volumeRecordCount}
                                        onChange={(e) => setVolumeRecordCount(Number(e.target.value))}
                                        className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                                    >
                                        <option value={5}>Last 5</option>
                                        <option value={10}>Last 10</option>
                                        <option value={30}>Last 30</option>
                                    </select>
                                    </div>
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
                                            <span id="weight-change" className={`font-semibold ${weightChangeColor}`}>
                                                {weightChangeText}
                                            </span>
                                        </div>
                                        <div>
                                            <span className="text-gray-600">Rate:</span>
                                            <span id="weight-rate" className={`font-semibold ${weightChangeColor}`}>
                                                {weightRateText}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            
                                {/* <!-- Weight Input Form --> */}
                                <div className="mt-4 border-t pt-4">
                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                        <div>
                                            <label className="block text-gray-700 text-sm mb-1">Date</label>
                                            <input 
                                            type="date" 
                                            id="weight-date" 
                                            value={inputDate}
                                            onChange={(e) => setInputDate(e.target.value)}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"/>
                                        </div>
                                        <div>
                                            <label className="block text-gray-700 text-sm mb-1">Weight (kg)</label>
                                            <input 
                                            type="number" 
                                            id="weight-value" 
                                            step="0.1" 
                                            value={inputWeight}
                                            onChange={(e) => setInputWeight(e.target.value)}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500" 
                                            placeholder="e.g. 75.5"/>
                                        </div>
                                        <div className="flex items-end">
                                            <button 
                                            id="add-weight-btn" 
                                            onClick={() => {
                                                if (!inputWeight || isNaN(Number(inputWeight))) {
                                                    setInputMessage('請輸入有效的體重');
                                                    return;
                                                }
                                                submitTodayWeight(inputDate, parseFloat(inputWeight));
                                                setInputMessage(' 體重已記錄！');
                                                setInputWeight('');
                                            }}
                                            className="w-full bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition">
                                                Add Record
                                            </button>
                                        </div>
                                    </div>
                                    <div 
                                    id="weight-input-message"   
                                    className={`mt-2 text-sm ${inputMessage ? 'text-green-600' : 'text-red-600'}`}
                                    >  
                                        {inputMessage}
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