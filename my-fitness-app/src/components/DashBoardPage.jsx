import { useState, useEffect, useRef } from 'react';
import './DashBoardPage.css'
import axiosInstance from "../api/axiosInstance.js";

function DashBoardPage({userId}){

    const [plans, setPlans] = useState([]);
    
    useEffect(() => {
        if (!userId) return;
        axiosInstance.get(`/user/${userId}/templates`)
        .then(res => setPlans(res.data))
        .catch(err => console.error("取的所有 templates 失敗", err));
    }, [userId]);

    //從後端抓取動作資料
    const [workoutTypes, setWorkoutTypes] = useState([]);

    useEffect(() => {
        axiosInstance.get("/workout-types")
        .then(res => setWorkoutTypes(res.data))
        .catch(err => console.error("取得動作清單失敗", err));
    }, []);

    const [showOptionsId, setShowOptionsId] = useState(null);
    const [deleteId, setDeleteId] = useState(null); // 記錄要刪除哪個 ID
    const [editPlan, setEditPlan] = useState(null);  // null 或 plan 物件
        
    useEffect(() => {
        const handleClickOutside = () => {
            setShowOptionsId(null);
        };
        if (showOptionsId) {
            document.addEventListener('click', handleClickOutside);
        }
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, [showOptionsId]);

    //點擊卡片後用selectedPlan存取get回傳
    const [selectedPlan, setSelectedPlan] = useState(null);
    const handleCardClick = (templateId) => {
        axiosInstance
        .get(`/user/${userId}/template/${templateId}`)
        .then(res => {
        setSelectedPlan(res.data); 
        })
        .catch(err => {
        console.error("取得模板詳細內容失敗", err);
        });
    };

    //點擊單張卡片編輯選項
    const handleEdit = (templateId) => {
        axiosInstance.get(`/user/${userId}/template/${templateId}`)
            .then(res => {
                const converted = {
                ...res.data,
                exercises: res.data.exercises.map((ex) => ({
                    ...ex,
                    sets: ex.sets.map((set) => ({
                    ...set,
                    id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), // 🔑 補上 id
                    })),
                })),
                };
                setEditPlan(converted);
            })
            .catch(err => {
            console.error("取得詳細資料失敗", err);
            alert("讀取詳細資料失敗");
            });
    }; 

    // 卡片呈現
    const TrainingPlanCard = ({ plan }) => {
        const dropdownRef = useRef();

        // 監聽點擊外部事件
        useEffect(() => {
          const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
              setShowOptionsId(null);
            }
          };
      
          if (showOptionsId === plan.id) {
            document.addEventListener('mousedown', handleClickOutside);
          }
      
          return () => {
            document.removeEventListener('mousedown', handleClickOutside);
          };
        }, [showOptionsId, plan.id, setShowOptionsId]);

        return (
            <div
            onClick={() => {
                    if (showOptionsId !== plan.id) {
                        handleCardClick(plan.id);
                    }
                }
            }
            className="group bg-white rounded-lg overflow-hidden subtle-shadow card flex-shrink-0 transition-colors duration-300 cursor-pointer flex flex-col justify-between"
            style={{ width: '300px', height:'300px' }} // ✅ 固定高度
            >
                
            <div className="group-hover:bg-neutral-800 h-2 bg-warm-gray-300 transition-colors duration-300"></div>

            {/* 內容主區塊 */}
            <div className="flex-1 p-6 flex flex-col">
                {/* 標題列 */}
                <div className="flex justify-between items-start mb-4">
                <h3 className="text-lg font-bold text-warm-black">{plan.title}</h3>
                <span className="px-3 py-1 bg-neutral-100 text-sm rounded-md">
                    {plan.plannedDate}
                </span>
                </div>

                {/* 訓練動作區塊（可滾動） */}
                <div className="space-y-3 overflow-y-auto pr-1 mb-4" style={{ maxHeight: '140px' }}>
                {plan.exercises.map((exercise, index) => (
                    <div key={`${exercise}-${index}`} className="flex items-center justify-between">
                    <div className="flex items-center">
                        <div className="w-2 h-2 bg-neutral-800 rounded-full mr-2"></div>
                        <span>{exercise.typeName}</span>
                    </div>
                    <span className="text-md mr-6 text-gray-700">x {exercise.sets}</span>
                    </div>
                ))}
                </div>

                {/* 固定底部區塊 */}
                <div className="relative flex  justify-between items-center mt-auto pt-2">
                <div className="flex space-x-2">
                    {plan.mainTags.slice(0, 3).map((tag, index) => (
                    <span key={index} className="px-2 py-1 bg-gray-100 text-xs rounded-full">
                        {tag}
                    </span>
                    ))}
                </div>
                <button
                    onClick={(e) => {
                    e.stopPropagation();
                    setShowOptionsId(showOptionsId === plan.id ? null : plan.id);
                    }}
                    className="p-2 border border-gray-200 rounded-md hover:bg-gray-100 transition-colors"
                >
                    <svg
                        className="w-5 h-5 text-gray-600"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"
                        ></path>
                    </svg>
                </button>

                {showOptionsId === plan.id && (
                    <div 
                    ref={dropdownRef}
                    onClick={(e)=>{e.stopPropagation();}}
                    className=" absolute right-4 bottom-6 w-24 bg-white border border-gray-200 rounded-lg shadow-lg z-10">
                        <button className="w-full text-left px-4 py-2 text-sm hover:bg-gray-100" onClick={(e) => {e.stopPropagation(); handleEdit(plan.id);}}>編輯</button>
                        <button className="w-full text-left px-4 py-2 text-sm hover:bg-gray-100" onClick={(e) => {e.stopPropagation(); setDeleteId(plan.id)}}>刪除</button>
                    </div>
                )}
                </div>
            </div>
            </div>
        );
    };

    const handleExerciseSetChange = (exIdx, setIdx, field, value) => {
        const updatedExercises = [...editPlan.exercises];
        updatedExercises[exIdx].sets[setIdx][field] = value;
        setEditPlan({ ...editPlan, exercises: updatedExercises });
    };
      
    const handleAddSet = (exIdx) => {
        const updatedExercises = [...editPlan.exercises];
        updatedExercises[exIdx].sets.push({id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), reps: '', weight: '' });
        setEditPlan({ ...editPlan, exercises: updatedExercises });
    };
      
    const handleRemoveSet = (exIdx, setIdx) => {
        const updatedExercises = [...editPlan.exercises];
        updatedExercises[exIdx].sets = updatedExercises[exIdx].sets.filter((_, i) => i !== setIdx);
        setEditPlan({ ...editPlan, exercises: updatedExercises });    
    };

    const handleDeleteExercise = (exIdx) => {
        const updatedExercises = [...editPlan.exercises];
        updatedExercises.splice(exIdx, 1);
        setEditPlan({ ...editPlan, exercises: updatedExercises });
    };

    const   emptyPlan = {
        id: null,
        title: '',
        tags: [],
        plannedDate: new Date().toISOString().slice(0, 10), // 預設為今天 yyyy-mm-dd
        exercises: [{name: '', sets: [{id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), reps: '', weight: '' }]}],
    };
    
    // 新增動作邏輯
    const [showExerciseModal, setShowExerciseModal] = useState(false); // 控制 modal 開關
    const [selectedMainTag, setSelectedMainTag] = useState('All');      // 目前選取的主分類
    const [searchKeyword, setSearchKeyword] = useState('');            // 搜尋關鍵字


    const filteredWorkoutTypes = workoutTypes.filter((type) => {
        const matchesTag = selectedMainTag === 'All' || type.mainTag === selectedMainTag;
        const matchesKeyword = type.name.toLowerCase().includes(searchKeyword.toLowerCase());
        return matchesTag && matchesKeyword;
    });

    //編輯、新增後變更當前的內容// post put 
    const [editingExerciseIndex, setEditingExerciseIndex] = useState(null);
    const addExerciseFromType = (type) => {
        if (editingExerciseIndex !== null) {
          // 編輯現有的
          setEditPlan(prev => {
            const updatedExercises = [...prev.exercises];
            updatedExercises[editingExerciseIndex] = {
              ...updatedExercises[editingExerciseIndex],
              typeId: type.id,   //後端post/put要求
              typeName: type.name, // ✅ 只改名字，保留 sets
              mainTag: type.mainTag,
            };
            return {
              ...prev,
              exercises: updatedExercises
            };
          });
      
          setEditingExerciseIndex(null);  // 清空狀態
        } else {
          // 新增新的
          const newExercise = {
            typeId: type.id,    //後端post/put要求
            typeName: type.name,//不是dto要求，是前端要求的來自workoutTypeDTO
            mainTag: type.mainTag,
            sets: [{id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), reps: '', weight: '' }]
          };
      
          setEditPlan(prev => ({
            ...prev,
            exercises: [...prev.exercises, newExercise]
          }));
        }
        setShowExerciseModal(false); // 關閉選單
    };

    useEffect(() => {
        if (selectedPlan || editPlan) {
          document.body.style.overflow = 'hidden'; // ✅ 鎖住 scroll
        } else {
          document.body.style.overflow = 'auto'; // ✅ 還原 scroll
        }
      
        // 預防 unmount 沒有回復
        return () => {
          document.body.style.overflow = 'auto';
        };
    }, [selectedPlan, editPlan]);

    //編輯新增卡片後送出按鈕
    const handleSavePlan = () => {
        if (
          editPlan.exercises.some(
            (exercise) => (exercise.typeName || exercise.name).trim() === '' || exercise.sets.length === 0
          ) ||
          editPlan.title.trim() === ''
        ) {
          alert('請填寫所有欄位');
          return;
        }
      
        const payload = {
          title: editPlan.title,
          plannedDate: editPlan.plannedDate,
          exercises: editPlan.exercises.map((exercise) => ({
            typeId: exercise.typeId,  // 取你實際的欄位名稱
            sets: exercise.sets.map((set) => ({
              reps: Number(set.reps),
              weight: Number(set.weight),
            })),
          })),
        };
      
        const action = (editPlan.id === null)
          ? axiosInstance.post(`/user/${userId}/template`, payload)
          : axiosInstance.put(`/user/${userId}/template/${editPlan.id}`, payload);
      
        action
          .then(() => {
            setEditPlan(null); // 關閉 modal
            return axiosInstance.get(`/user/${userId}/templates`);
          })
          .then(res => {
            setPlans(res.data); 
          })
          .catch(err => {
            console.error('儲存失敗：', err);
            alert('儲存失敗，請稍後再試');
          });
    };

    //刪除卡片按鈕
    const handleDeletePlan = (planId) => {
        axiosInstance.delete(`/user/${userId}/template/${planId}`)
        .then(() => {
        // 刪除後重新抓一次所有卡片
        return axiosInstance.get(`/user/${userId}/templates`);
        })
        .then(res => {
        setPlans(res.data);
        setDeleteId(null);  // 關閉 modal
        })
        .catch((err) => alert("刪除失敗，請稍後再試" + err));
    };

    //通過卡片新增訓練紀錄
    const handleStartTraining = (templateId) => {
        axiosInstance
            .post(`/user/${userId}/session/from-template/${templateId}`)
            .then(() => {
                setSelectedPlan(null);
            })
            .catch((err) => {alert('建立訓練紀錄失敗') + (err.response?.data?.message || err.message)});
    };
      
    return (
    <div className="bg-neutral-100">    
         {/* <!-- 主要內容 --> */}
         <div className="flex-1 overflow-y-auto">
            {/* <!-- 頂部導航 --> */}
            <div className="bg-white subtle-shadow ">
                <div className="container mx-auto px-6 py-4 flex justify-between items-center">
                    <div className="flex items-center">
                        <h1 className="text-2xl font-bold text-gray-900">儀表板</h1>
                    </div>
                    <div className="flex items-center space-x-4">
                        <button className="p-2 rounded-full hover:bg-warm-gray-100">
                            <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
                            </svg>
                        </button>
                        <button className="p-2 rounded-full hover:bg-warm-gray-100">
                            <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
    
            {/* <!-- 主要內容區域 --> */}
            <main className="container mx-auto px-6 py-8">
                {/* <!-- 狀態指標區 --> */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
                    <div className="bg-white rounded-lg p-6 subtle-shadow status-indicator">
                        <div className="flex items-center justify-between">
                            <div>
                                <div className="text-sm text-gray-500 mb-1">本週訓練</div>
                                <div className="text-2xl font-bold text-warm-black">4 次</div>
                                <div className="text-sm  mt-1">目標達成 80%</div>
                            </div>
                            <div className="w-14 h-14 relative">
                                <svg className="w-full h-full" viewBox="0 0 36 36">
                                    <circle cx="18" cy="18" r="16" fill="none" stroke="#e5e2dd" strokeWidth="3"></circle>
                                    <circle className="progress-ring-circle" cx="18" cy="18" r="16" fill="none" stroke="black" strokeWidth="3" strokeDasharray="100" strokeDashoffset="20"></circle>
                                </svg>
                                <div className="absolute inset-0 flex items-center justify-center text-sm font-medium">80%</div>
                            </div>
                        </div>
                    </div>
                    
                    <div className="bg-white rounded-lg p-6 subtle-shadow status-indicator">
                        <div className="flex items-center justify-between">
                            <div>
                                <div className="text-sm text-gray-500 mb-1">總訓練量</div>
                                <div className="text-2xl font-bold text-warm-black">5,280 kg</div>
                                <div className="text-sm  mt-1">較上週 +12%</div>
                            </div>
                            <div className="w-14 h-14 bg-neutral-100 rounded-full flex items-center justify-center">
                                <svg className="w-6 h-6 " fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                                </svg>
                            </div>
                        </div>
                    </div>
                    
                    <div className="bg-white rounded-lg p-6 subtle-shadow status-indicator">
                        <div className="flex items-center justify-between">
                            <div>
                                <div className="text-sm text-gray-500 mb-1">連續訓練</div>
                                <div className="text-2xl font-bold text-warm-black">12 天</div>
                                <div className="text-sm  mt-1">個人最佳紀錄</div>
                            </div>
                            <div className="w-14 h-14 bg-neutral-100 rounded-full flex items-center justify-center">
                                <svg className="w-6 h-6 " fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                </svg>
                            </div>
                        </div>
                    </div>
                    
                    <div className="bg-white rounded-lg p-6 subtle-shadow status-indicator">
                        <div className="flex items-center justify-between">
                            <div>
                                <div className="text-sm text-gray-500 mb-1">下次訓練</div>
                                <div className="text-2xl font-bold text-warm-black">今天</div>
                                <div className="text-sm  mt-1">胸部 + 三頭肌</div>
                            </div>
                            <div className="w-14 h-14 bg-neutral-100 rounded-full flex items-center justify-center">
                                <svg className="w-6 h-6 " fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                                </svg>
                            </div>
                        </div>
                    </div>
                </div>
                
                {/* <!-- 訓練計劃區 - 水平滾動 --> */}
                <div className="mb-10">
                    <div className="flex justify-between items-center mb-6">
                        <h2 className="text-xl font-bold text-warm-black">我的訓練計劃</h2>
                        <div className="flex items-center space-x-3">
                            <button id="scrollLeft" className="p-2 rounded-full bg-warm-gray-100 hover:bg-neutral-300 transition-colors">
                                <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button id="scrollRight" className="p-2 rounded-full bg-warm-gray-100 hover:bg-neutral-300 transition-colors">
                                <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                                </svg>
                            </button>
                            <button onClick={() => setEditPlan(emptyPlan)}
                                    className="px-4 py-2 bg-neutral-800 text-white rounded-md hover:bg-neutral-600 transition-colors flex items-center space-x-2">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                                </svg>
                                <span>新增計劃</span>
                            </button>
                        </div>
                    </div>
                    
                    {/* 卡片區 */}
                    <div className="relative">
                        <div id="templateContainer" className="flex overflow-x-auto pb-4 template-scroll gap-4">
                            <div className="flex gap-4">

                                {/* 卡片狀態，從路徑獲得資料後傳到plans */}
                                {plans && plans.length > 0 ? (
                                    plans.map((plan) => (
                                    <TrainingPlanCard key={plan.id} plan={plan} onClick={() => handleCardClick(plan.id)} />
                                    ))
                                ) : (
                                    
                                    <div className="bg-transparent"
                                    style={{ width: '300px', height:'300px'}} // ✅ 固定高度
                                    >
                                    </div>
                                )}

                                {/* 卡片編輯modal */}
                                {editPlan && (
                                    <div
                                        className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            if (e.target === e.currentTarget) {
                                                setEditPlan(null);  // 點背景關閉
                                            }
                                        }}
                                    >
                                        <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg max-h-[80vh]">
                                            <h2 className="text-lg font-bold mb-4">
                                                {editPlan?.id === null ? '新增訓練計畫' : '編輯訓練計畫'}
                                            </h2>

                                            {/* 計畫名稱、設定日期編輯 */}
                                            <div className="mb-4 grid grid-cols-2 gap-4">
                                            {/* 計劃名稱欄位 */}
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700 mb-1">計劃名稱</label>
                                                <input
                                                type="text"
                                                value={editPlan.title}
                                                onChange={(e) => setEditPlan({ ...editPlan, title: e.target.value })}
                                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                                                />
                                            </div>

                                            {/* 日期欄位 */}
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700 mb-1 ml-1">日期</label>
                                                <input
                                                type="date"
                                                value={editPlan.plannedDate}
                                                onChange={(e) => setEditPlan({ ...editPlan, plannedDate: e.target.value })}
                                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                                                />
                                            </div>
                                            </div>

                                            {/* 訓練項目編輯 */}
                                            <label className="block text-sm font-medium text-gray-700 mb-1">訓練項目</label>
                                            <div className="mb-4 max-h-56 overflow-y-auto pr-1">
                                                {editPlan.exercises.map((exercise, index) => (
                                                    <div key={`${exercise}-${index}`} className="bg-gray-50 rounded p-1 pr-2 duration-200 hover:shadow-lg hover:-translate-y-0.5 mb-2 ml-3 mr-3 mt-3">
                                                        {/* 動作名稱輸入框 */}
                                                        
                                                        <div className="flex items-center space-x-2 mb-2">
                                                        <div
                                                            className="flex-1 font-semibold text-gray-700 px-4 ml-2 py-2 bg-gray-50 "
                                                        >
                                                            {exercise.typeName || '選擇動作'}
                                                        </div>

                                                        {/* 變更動作按鈕，點擊後跳出選擇動作modal */}
                                                        <button 
                                                        onClick={() => {
                                                            setEditingExerciseIndex(index);
                                                            setShowExerciseModal(true);
                                                            }}>
                                                            <svg
                                                                className="pl-1 w-6 h-6 text-gray-400 hover:text-gray-700"
                                                                fill="none"
                                                                stroke="currentColor"
                                                                viewBox="0 0 24 24"
                                                                xmlns="http://www.w3.org/2000/svg"
                                                            >
                                                                <path
                                                                    strokeLinecap="round"
                                                                    strokeLinejoin="round"
                                                                    strokeWidth="2"
                                                                    d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"
                                                                ></path>
                                                            </svg>
                                                        </button>
                                                        <button 
                                                            onClick={() => handleDeleteExercise(index)}
                                                            type="button"
                                                            className=" text-gray-400 hover:text-gray-700 bg-gray-50 p-2 rounded-full">
                                                                <svg xmlns="http://www.w3.org/2000/svg" className=" h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                                </svg>
                                                        </button>
                                                        </div>


                                                        {/* 多組 reps/weight */}
                                                        <div className="space-y-2 mt-2">
                                                        {exercise.sets.map((set, setIndex) => (
                                                            <div key={set.id} className="flex items-center gap-2">
                                                            <label className="text-xs text-gray-500 ml-6">次數 (Reps)</label>
                                                            <input
                                                                type="text"
                                                                value={set.reps}
                                                                onChange={(e) => handleExerciseSetChange(index, setIndex, 'reps', e.target.value)}
                                                                className="w-1/4 px-2 py-1 rounded text-center border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                                                            />
                                                            <span className="mx-1 ml-8">×</span>
                                                            <label className="text-xs text-gray-500 ml-6">重量 (Reps)</label>

                                                            <input
                                                                type="text"
                                                                value={set.weight}
                                                                onChange={(e) => handleExerciseSetChange(index, setIndex, 'weight', e.target.value)}
                                                                className="w-1/4 px-2 py-1 rounded text-center border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                                                            />
                                                            <label className="text-xs text-gray-500 ml-2 ">kg</label>

                                                            
                                                            <button onClick={() => handleRemoveSet(index, setIndex)} type="button" className="remove-set pl-2 text-gray-400 hover:text-gray-700 bg-gray-50  p-1.5 rounded-full">
                                                                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                                                                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM7 9a1 1 0 000 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
                                                                </svg>
                                                            </button>

                                                            </div>
                                                        ))}
                                                        </div>

                                                        {/* 新增一組 */}
                                                        <button
                                                        onClick={() => handleAddSet(index)}
                                                        className="m-2 ml-6 text-sm text-neutral-700 hover:underline"
                                                        >
                                                        + 新增訓練組
                                                        </button>
                                                    </div>
                                                    ))}

                                                    {/* 新增整體動作 */}
                                                    <div className="px-3 mb-4">
                                                        <button
                                                        onClick={() => setShowExerciseModal(true)}
                                                        className="w-full py-2 mt-3 text-sm text-white bg-neutral-800 hover:bg-neutral-600 rounded"
                                                        >
                                                        + 新增動作
                                                        </button>
                                                    </div>
                                            </div>

                                            {/* Tags */}
                                            <div className="mb-2">
                                                <div className="flex flex-wrap gap-2">
                                                    {editPlan.exercises.filter(ex => ex.mainTag).map((exercise, index) => (
                                                    <span key={`${exercise}-${index}`} className="px-3 py-1 bg-gray-100 text-sm rounded-full">
                                                        {exercise.mainTag}
                                                    </span>
                                                    ))}
                                                </div>
                                            </div>

                                            {/* 更多選項按鈕 */}
                                            <div className="flex justify-end mt-6 space-x-3">
                                                <button
                                                    className="px-4 py-2 rounded bg-gray-200  hover:bg-gray-300 transition"
                                                    onClick={() => setEditPlan(null)}
                                                >
                                                    取消
                                                </button>
                                                <button
                                                className="px-4 py-2 flex justify-end rounded bg-neutral-800 text-white hover:bg-neutral-600 transition"
                                                onClick={handleSavePlan}
                                                >
                                                {editPlan?.id === null ? '新增' : '儲存修改'}
                                                </button>

                                            </div>
                                        </div>
                                    </div>
                                )}

                            {/* 刪除計劃 */}
                            {deleteId && (
                                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                                        onClick={(e) => {
                                            if (e.target === e.currentTarget) {
                                                setDeleteId(null);
                                            }
                                        }}
                                    >
                                        <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
                                            <h2 className="text-lg font-bold mb-4">確認刪除</h2>
                                            <p>你確定要刪除這個訓練計劃嗎？這個動作無法復原。</p>
                                            <div className="flex justify-end mt-6 space-x-3">
                                                <button
                                                    className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300 transition"
                                                    onClick={() => setDeleteId(null)}
                                                >
                                                    取消
                                                </button>
                                                <button
                                                    className="px-4 py-2 rounded bg-red-600 text-white hover:bg-red-700 transition"
                                                    onClick={() => handleDeletePlan(deleteId)}
                                                >
                                                    確認刪除
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>  

                            {/* 點擊卡片modal */}
                            {selectedPlan && (
                                <div
                                    onClick={(e) => {

                                    if (e.target === e.currentTarget) setSelectedPlan(null);
                                    }}
                                    className="fixed inset-0 z-50 bg-black bg-opacity-50 flex justify-center items-center"
                                >
                                    <div className="bg-white rounded w-full max-w-lg max-h-[80vh] flex flex-col">
                                    {/* Header */}
                                    <div className="p-6 border-b">
                                        <h2 className="text-xl font-bold">{selectedPlan.title}</h2>
                                        <p className="text-gray-600">日期：{selectedPlan.plannedDate}</p>
                                    </div>

                                    {/* Scrollable Content */}
                                    <div className="flex-1 overflow-y-auto px-6 py-4 pl-10 space-y-4">
                                        {selectedPlan.exercises.map((exercise, index) => (
                                        <div key={`${exercise}-${index}`}>
                                            <p className="font-semibold mr-10 mb-1 flex justify-between">
                                                {exercise.typeName}
                                                <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded-full">
                                                    {exercise.mainTag}
                                                </span>
                                            </p>    
                                            <div className="text-sm text-gray-700">
                                            <div className="grid grid-cols-4 gap-x-2 font-medium text-gray-500 mb-1">
                                                <span>次數</span>
                                                <span></span>
                                                <span>重量</span>
                                                <span></span>
                                            </div>
                                                {exercise.sets.map((set, setIndex) => (
                                                    <div key={`${set.reps}-${set.weight}-${setIndex}`} className="grid grid-cols-4 gap-x-2">
                                                    <span>{set.reps}</span>
                                                    <span className="text-center">×</span>
                                                    <span>{set.weight}</span>
                                                    <span className="text-gray-500">kg</span>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                        ))}
                                    </div>

                                    {/* Fixed Bottom Buttons */}
                                    <div className="p-4 border-t flex justify-end space-x-3">
                                        <button
                                        onClick={() => {
                                            handleStartTraining(selectedPlan.id);
                                        }}
                                        className="px-4 py-2 w-full rounded bg-neutral-800 text-white hover:bg-neutral-600 transition"
                                        >
                                        開始訓練
                                        </button>
                                    </div>
                                </div>
                            </div>
                            )}

                            {/* 新增動作modal */}
                            {showExerciseModal && (
                                <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center" 
                                    onClick={e => e.target === e.currentTarget && setShowExerciseModal(false)}>
                                    <div className="bg-white w-full max-w-md max-h-[80vh] rounded-lg p-4 flex flex-col">
                                    <h2 className="text-lg font-bold mb-4">選擇訓練動作</h2>

                                        {/* 主分類 tag 列 */}
                                        <div className="flex flex-wrap gap-2 mb-4">
                                            {['All', 'Chest', 'Leg', 'Back', 'Shoulder'].map(tag => (
                                            <button
                                                key={tag === 'All' ? null : tag}
                                                onClick={() => setSelectedMainTag(tag)}
                                                className={`px-3 py-1 rounded-full border ${selectedMainTag === tag ? 'bg-neutral-800 text-white' : 'bg-gray-100'}`}
                                            >
                                                {tag}
                                            </button>
                                            ))}
                                        </div>

                                        {/* 搜尋框 */}
                                        <input
                                            type="text"
                                            value={searchKeyword}
                                            onChange={(e) => setSearchKeyword(e.target.value)}
                                            className="px-4 py-2 border rounded mb-3"
                                            placeholder="搜尋動作"
                                        />

                                        {/* 動作清單 */}
                                        <div className="overflow-y-auto flex-1 pr-1 space-y-2">
                                            {filteredWorkoutTypes.length === 0 ? (
                                            <p className="text-sm text-gray-500">找不到符合的動作</p>
                                            ) : (
                                            filteredWorkoutTypes.map((type) => (
                                                <button
                                                key={type.id}
                                                className="w-full px-4 py-2 text-left bg-gray-50 hover:bg-gray-100 rounded border text-sm"
                                                onClick={() => {
                                                    addExerciseFromType(type);
                                                    setShowExerciseModal(false);
                                                }}
                                                >
                                                {type.name}
                                                </button>
                                            ))
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
                
                {/* <!-- 月曆區域 --> */}
                <div className="mb-10">
                    <div className="flex justify-between items-center mb-6">
                        <h2 className="text-xl font-bold text-warm-black">訓練日曆</h2>
                        {/* <p>這裡要把日曆縮小放左邊然後旁邊加入一個區塊可能用來顯示上次訓練內容並且當點選日曆上日期顯示內容，日曆如果太小，不能對其左邊內容，可以在日曆下方放當周頻率，然後日曆可以思考要不要有不同呈現方式</p>
                        <p>然後日曆用標籤顯示，標籤顏色代表自定義內容，像是腿、胸推日、上胸日等等，同樣在旁邊顯示標籤對應內容</p> */}
                        <div className="flex items-center space-x-3">
                            <button id="prevMonth" className="p-2 rounded-full bg-warm-gray-100 hover:bg-warm-gray-200 transition-colors">
                                <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <span id="currentMonth" className="font-medium">2023 年 10 月</span>
                            <button id="nextMonth" className="p-2 rounded-full bg-warm-gray-100 hover:bg-warm-gray-200 transition-colors">
                                <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                                </svg>
                            </button>
                        </div>
                    </div>
                    
                    <div className="bg-white rounded-lg p-6 subtle-shadow">
                        {/* <!-- 星期標題 --> */}
                        <div className="grid grid-cols-7 mb-4">
                            <div className="text-center font-medium text-gray-500">日</div>
                            <div className="text-center font-medium text-gray-500">一</div>
                            <div className="text-center font-medium text-gray-500">二</div>
                            <div className="text-center font-medium text-gray-500">三</div>
                            <div className="text-center font-medium text-gray-500">四</div>
                            <div className="text-center font-medium text-gray-500">五</div>
                            <div className="text-center font-medium text-gray-500">六</div>
                        </div>
                        
                        {/* <!-- 日曆格子 --> */}
                        <div className="grid grid-cols-7 gap-2">
                            {/* <!-- 上個月的日期 --> */}
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">24</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">25</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">26</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">27</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">28</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">29</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">30</div>
                            </div>
                            
                            {/* <!-- 本月日期 --> */}
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">1</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">腿部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">2</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">3</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">胸部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">4</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">5</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">背部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">6</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">7</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">肩部日</div>
                            </div>
                            
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">8</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">9</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">腿部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">10</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">11</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">胸部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">12</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">13</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">背部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">14</div>
                            </div>
                            
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">15</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">肩部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">16</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">17</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">腿部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">18</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">19</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">胸部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">20</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">21</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">背部日</div>
                            </div>
                            
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">22</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">23</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  mt-1">肩部日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg bg-neutral-100 border border-accent">
                                <div className="text-sm font-bold">24</div>
                                <div className="flex justify-center mt-1">
                                    <div className="workout-dot bg-neutral-800"></div>
                                </div>
                                <div className="text-xs  font-medium mt-1">今日</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">25</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">26</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">27</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">28</div>
                            </div>
                            
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">29</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">30</div>
                            </div>
                            <div className="calendar-day p-1 text-center rounded-lg hover:bg-warm-gray-100 cursor-pointer">
                                <div className="text-sm">31</div>
                            </div>
                            
                            {/* <!-- 下個月的日期 --> */}
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">1</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">2</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">3</div>
                            </div>
                            <div className="calendar-day p-1 text-center text-gray-400">
                                <div className="text-sm">4</div>
                            </div>
                        </div>
                        
                        {/* <!-- 圖例 --> */}
                        <div className="mt-6 flex items-center justify-center space-x-6 text-sm">
                            <div className="flex items-center">
                                <div className="w-3 h-3 bg-neutral-800 rounded-full mr-2"></div>
                                <span>已完成訓練</span>
                            </div>
                            <div className="flex items-center">
                                <div className="w-3 h-3 border border-accent bg-neutral-100 rounded-full mr-2"></div>
                                <span>今日</span>
                            </div>
                            <div className="flex items-center">
                                <div className="w-3 h-3 border border-gray-300 rounded-full mr-2"></div>
                                <span>計劃中訓練</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                {/* <!-- 自訂圖表區域 --> */}
                <div>
                    <div className="flex justify-between items-center mb-6">
                        <h2 className="text-xl font-bold text-warm-black">我的圖表</h2>
                        <button className="px-4 py-2 bg-neutral-800 text-white rounded-md hover:bg-neutral-600 transition-colors flex items-center space-x-2">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                            </svg>
                            <span>新增圖表</span>
                        </button>
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {/* <!-- 圖表 1: 訓練量趨勢 --> */}
                        <div className="bg-white rounded-lg p-6 subtle-shadow">
                            <div className="flex justify-between items-center mb-4">
                                <h3 className="font-bold text-warm-black">訓練量趨勢</h3>
                                <div className="flex items-center space-x-2">
                                    <button className="p-1 rounded hover:bg-warm-gray-100">
                                        <svg className="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"></path>
                                        </svg>
                                    </button>
                                </div>
                            </div>
                            
                            <div className="chart-container">
                                <svg className="w-full h-full" viewBox="0 0 300 200">
                                    {/* <!-- 座標軸 --> */}
                                    <line x1="40" y1="170" x2="280" y2="170" stroke="#d6d2cc" strokeWidth="1" />
                                    <line x1="40" y1="20" x2="40" y2="170" stroke="#d6d2cc" strokeWidth="1" />
                                    
                                    {/* <!-- Y軸標籤 --> */}
                                    <text x="10" y="170" fontSize="10" textAnchor="middle">0</text>
                                    <text x="10" y="130" fontSize="10" textAnchor="middle">2k</text>
                                    <text x="10" y="90" fontSize="10" textAnchor="middle">4k</text>
                                    <text x="10" y="50" fontSize="10" textAnchor="middle">6k</text>
                                    
                                    {/* <!-- X軸標籤 --> */}
                                    <text x="40" y="185" fontSize="10" textAnchor="middle">週一</text>
                                    <text x="80" y="185" fontSize="10" textAnchor="middle">週二</text>
                                    <text x="120" y="185" fontSize="10" textAnchor="middle">週三</text>
                                    <text x="160" y="185" fontSize="10" textAnchor="middle">週四</text>
                                    <text x="200" y="185" fontSize="10" textAnchor="middle">週五</text>
                                    <text x="240" y="185" fontSize="10" textAnchor="middle">週六</text>
                                    <text x="280" y="185" fontSize="10" textAnchor="middle">週日</text>
                                    
                                    {/* <!-- 網格線 --> */}
                                    <line x1="40" y1="130" x2="280" y2="130" stroke="#e5e2dd" strokeWidth="1" strokeDasharray="4" />
                                    <line x1="40" y1="90" x2="280" y2="90" stroke="#e5e2dd" strokeWidth="1" strokeDasharray="4" />
                                    <line x1="40" y1="50" x2="280" y2="50" stroke="#e5e2dd" strokeWidth="1" strokeDasharray="4" />
                                    
                                    {/* <!-- 數據點 --> */}
                                    <circle cx="40" cy="120" r="4" fill="#8fb3a0" />
                                    <circle cx="80" cy="170" r="4" fill="#8fb3a0" />
                                    <circle cx="120" cy="90" r="4" fill="#8fb3a0" />
                                    <circle cx="160" cy="140" r="4" fill="#8fb3a0" />
                                    <circle cx="200" cy="60" r="4" fill="#8fb3a0" />
                                    <circle cx="240" cy="110" r="4" fill="#8fb3a0" />
                                    <circle cx="280" cy="170" r="4" fill="#8fb3a0" />
                                    
                                    {/* <!-- 連線 --> */}
                                    <polyline points="40,120 80,170 120,90 160,140 200,60 240,110 280,170" fill="none" stroke="#8fb3a0" strokeWidth="2" />
                                    
                                    {/* <!-- 區域填充 --> */}
                                    <path d="M40,120 L80,170 L120,90 L160,140 L200,60 L240,110 L280,170 L280,170 L40,170 Z" fill="#e8f0ec" fillOpacity="0.6" />
                                </svg>
                            </div>
                        </div>
                        
                        {/* <!-- 圖表 2: 肌群分佈 --> */}
                        <div className="bg-white rounded-lg p-6 subtle-shadow">
                            <div className="flex justify-between items-center mb-4">
                                <h3 className="font-bold text-warm-black">肌群訓練分佈</h3>
                                <div className="flex items-center space-x-2">
                                    <button className="p-1 rounded hover:bg-warm-gray-100">
                                        <svg className="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"></path>
                                        </svg>
                                    </button>
                                </div>
                            </div>
                            
                            <div className="chart-container">
                                <svg className="w-full h-full" viewBox="0 0 300 200">
                                    {/* <!-- 圓餅圖 --> */}
                                    <g transform="translate(150, 100)">
                                        {/* <!-- 胸部 25% --> */}
                                        <path d="M0,0 L0,-80 A80,80 0 0,1 69.28,40 z" fill="#8fb3a0" />
                                        {/* <!-- 背部 20% --> */}
                                        <path d="M0,0 L69.28,40 A80,80 0 0,1 -27.71,74.64 z" fill="#a0c1b0" />
                                        {/* <!-- 腿部 20% --> */}
                                        <path d="M0,0 L-27.71,74.64 A80,80 0 0,1 -80,0 z" fill="#b2cfc1" />
                                        {/* <!-- 肩部 15% --> */}
                                        <path d="M0,0 L-80,0 A80,80 0 0,1 -27.71,-74.64 z" fill="#c4ddd2" />
                                        {/* <!-- 手臂 15% --> */}
                                        <path d="M0,0 L-27.71,-74.64 A80,80 0 0,1 69.28,-40 z" fill="#d6ebe3" />
                                        {/* <!-- 核心 5% --> */}
                                        <path d="M0,0 L69.28,-40 A80,80 0 0,1 0,-80 z" fill="#e8f9f4" />
                                    </g>
                                    
                                    {/* <!-- 圖例 --> */}
                                    <g transform="translate(240, 80)">
                                        <rect x="0" y="0" width="10" height="10" fill="#8fb3a0" />
                                        <text x="15" y="9" fontSize="10">胸部 25%</text>
                                        
                                        <rect x="0" y="20" width="10" height="10" fill="#a0c1b0" />
                                        <text x="15" y="29" fontSize="10">背部 20%</text>
                                        
                                        <rect x="0" y="40" width="10" height="10" fill="#b2cfc1" />
                                        <text x="15" y="49" fontSize="10">腿部 20%</text>
                                        
                                        <rect x="0" y="60" width="10" height="10" fill="#c4ddd2" />
                                        <text x="15" y="69" fontSize="10">肩部 15%</text>
                                        
                                        <rect x="0" y="80" width="10" height="10" fill="#d6ebe3" />
                                        <text x="15" y="89" fontSize="10">手臂 15%</text>
                                        
                                        <rect x="0" y="100" width="10" height="10" fill="#e8f9f4" />
                                        <text x="15" y="109" fontSize="10">核心 5%</text>
                                    </g>
                                </svg>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
    );
}

export default DashBoardPage;
