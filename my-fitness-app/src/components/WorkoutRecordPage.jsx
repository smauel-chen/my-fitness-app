import './WorkoutRecordPage.css'

import { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';


function WorkoutRecordPage({userId}) {

    // 👉 搜尋欄位
    const [searchTerm, setSearchTerm] = useState('');

    // 👉 篩選器的 tag 狀態
    const [activeFilters, setActiveFilters] = useState(new Set());
    // 👉 用於 tag filter modal
    const [isTagFilterModalOpen, setIsTagFilterModalOpen] = useState(false);

    //所有紀錄
    const [records, setRecords] = useState([]);
    //單份紀錄detail & modal control
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [detailRecord, setDetailRecord] = useState(null);
    
    //從後端抓取動作資料
    const [workoutTypes, setWorkoutTypes] = useState([]);
    const [showExerciseModal, setShowExerciseModal] = useState(false);  //選擇動作modal
    const [searchKeyword, setSearchKeyword] = useState('');             // 搜尋關鍵字
    const [selectedMainTag, setSelectedMainTag] = useState('All');      // 目前選取的主分類

    useEffect(() => {
        axiosInstance.get("/workout-types")
        .then(res => setWorkoutTypes(res.data))
        .catch(err => console.error("取得動作清單失敗", err));
    }, []); 
    
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
          setCurrentRecord(prev => {
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
      
          setCurrentRecord(prev => ({
            ...prev,
            exercises: [...prev.exercises, newExercise]
          }));
        }
        setShowExerciseModal(false); // 關閉選單
    };

    const handleExerciseSetChange = (exIdx, setIdx, field, value) => {
        const updatedExercises = [...currentRecord.exercises];
        updatedExercises[exIdx].sets[setIdx][field] = value;
        setCurrentRecord({ ...currentRecord, exercises: updatedExercises });
    };
      
    const handleAddSet = (exIdx) => {
        const updatedExercises = [...currentRecord.exercises];
        updatedExercises[exIdx].sets.push({id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), reps: '', weight: '' });
        setCurrentRecord({ ...currentRecord, exercises: updatedExercises });
    };
      
    const handleRemoveSet = (exIdx, setIdx) => {
        const updatedExercises = [...currentRecord.exercises];
        updatedExercises[exIdx].sets = updatedExercises[exIdx].sets.filter((_, i) => i !== setIdx);
        setCurrentRecord({ ...currentRecord, exercises: updatedExercises });    
    };

    const handleDeleteExercise = (exIdx) => {
        const updatedExercises = [...currentRecord.exercises];
        updatedExercises.splice(exIdx, 1);
        setCurrentRecord({ ...currentRecord, exercises: updatedExercises });
    };

    //獲得所有紀錄
    useEffect(() => {
    axiosInstance.get(`/user/${userId}/sessions`)
        .then(res => {
        const formatted = res.data.map(s => ({
            sessionId: s.sessionId,
            title: s.title,
            date: s.date,
            tags: s.mainTags ?? []  // 🔹確保即使為 null 也能正常顯示
        }));
        setRecords(formatted);
        })  
        .catch(err => {
        console.error('❌ 取得訓練紀錄失敗：', err);
        });
    }, [userId]);

    //點擊單份紀錄、獲得詳細內容
    const handleViewRecord = (sessionId) => {
        axiosInstance
            .get(`/user/${userId}/session/${sessionId}`)
            .then((res) => {
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
                setDetailRecord(converted);
                setIsDetailModalOpen(true);
            })
            .catch((err) => alert("無法取得訓練紀錄，請稍後再試" + err));
    };

    // Modal 開關
    const [isRecordModalOpen, setIsRecordModalOpen] = useState(false);
    // 👉 當前要編輯/查看的紀錄
    const [currentRecord, setCurrentRecord] = useState(null);
    //新增紀錄的空白紀錄
    const emptyRecord = {
        id: null,
        title: '',
        date: new Date().toISOString().slice(0, 10), // 預設為今天 yyyy-mm-dd
        exercises: [{typeId:null, typeName: '', mainTag:'', secondaryTags: [] ,sets: [{id:crypto.randomUUID?.() ?? Date.now()() + Math.random(), reps: '', weight: '' }]}],
    };//這裡次要標籤空白語法要確認

    //獲得要編輯的資料
    const handleEdit = (sessionId) => {
        if(sessionId !== null){
            axiosInstance
                .get(`/user/${userId}/session/${sessionId}`)
                .then((res) => {
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
                    setCurrentRecord(converted);
                    setIsRecordModalOpen(true);
                })
                .catch((err) => alert("獲取紀錄失敗" + err));
        } else {
            setCurrentRecord(emptyRecord);
            setIsRecordModalOpen(true);
        }
    }
    
    //編輯、新增後送出按鈕
    const handleSavePlan = () => {
        if (
            currentRecord.exercises.some(
            (exercise) => (exercise.typeName || exercise.name).trim() === '' || exercise.sets.length === 0
            ) ||
            currentRecord.title.trim() === ''
        ) {
            alert('請填寫所有欄位');
            return;
        }
        
        const payload = {
            title: currentRecord.title,
            date: currentRecord.date,
            exercises: currentRecord.exercises.map((exercise) => ({
                typeId: exercise.typeId,  
                sets: exercise.sets.map((set) => ({
                    reps: Number(set.reps),
                    weight: Number(set.weight),
                })),
            })),
        };
        
        const action = (currentRecord.id === null)
            ? axiosInstance.post(`/user/${userId}/session`, payload)
            : axiosInstance.put(`/user/${userId}/session/${currentRecord.id}`, payload);
        action
            .then(() => {
            setIsRecordModalOpen(false); // 關閉 modal
            setIsDetailModalOpen(false);
            return axiosInstance.get(`/user/${userId}/sessions`);
            })
            .then(res => {
                const formatted = res.data.map(s => ({
                    sessionId: s.sessionId,
                    title: s.title,
                    date: s.date,
                    tags: s.mainTags ?? []  // 🔹確保即使為 null 也能正常顯示
                }));
                setRecords(formatted);
            })
            .catch(err => {
                alert('儲存失敗，請稍後再試' + (err.response?.data?.message || err.message));
            });
    };

    const [deleteId, setDeleteId] = useState(null);
    const handleDeleteRecord = (sessionId) => {
        axiosInstance
            .delete(`/user/${userId}/session/${sessionId}`)
            .then(() => {
                return axiosInstance.get(`/user/${userId}/sessions`);
            })
            .then(res => {
                const formatted = res.data.map(s => ({
                    sessionId: s.sessionId,
                    title: s.title,
                    date: s.date,
                    tags: s.mainTags ?? []  // 🔹確保即使為 null 也能正常顯示
                }));
                setRecords(formatted);

            setDeleteId(null);
            })
            .catch(err => {
                alert('儲存失敗，請稍後再試' + (err.response?.data?.message || err.message));
            });
    }

    const filteredRecords = records.filter((record) => {
        const matchesSearch =
          searchTerm === '' ||
          record.title.toLowerCase().includes(searchTerm.toLowerCase());
      
        const matchesTags =
          activeFilters.size === 0 ||
          record.tags?.some((tag) => activeFilters.has(tag));
      
        return matchesSearch && matchesTags;
      });
      
    // ✅ 改成正確的 Set 操作
    const handleRemoveFilter = (tagToRemove) => {
        setActiveFilters((prev) => {
            const updated = new Set(prev);
            updated.delete(tagToRemove);
            return updated;
        });
    };

    return (
    <div>
        {/* <!-- Main Content --> */}
        <div >
            <div className="flex-1 overflow-y-auto">
                <div className="bg-white subtle-shadow">
                    <div className="container mx-auto px-6 py-4 flex justify-between items-center">
                        <div className="flex items-center">
                            <h1 className="text-2xl font-bold text-gray-900">訓練紀錄</h1>
                        </div>
                    </div>
                </div>
                {/* <!-- Content Area --> */}
                <div className="p-4 md:p-6">
                    <div className="max-w-6xl mx-auto">
                        {/* <!-- Controls --> */}
                        <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                            <div className="flex flex-col md:flex-row gap-3">
                                <div className="flex-1">
                                    <input  type="text" placeholder="Search training records..." 
                                            value={searchTerm} 
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"/>
                                </div>
                                <div className="flex gap-2">
                                    <button onClick={() => {
                                                handleEdit(null);
                                            }}
                                            className="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition">
                                        Add Record
                                    </button>
                                    <button onClick={() => {//id = tag-filter-btn
                                                setIsTagFilterModalOpen(true);
                                            }}
                                            className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition flex items-center gap-1">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
                                            <line x1="7" y1="7" x2="7.01" y2="7"></line>
                                        </svg>
                                        Tags
                                    </button>
                                </div>
                            </div>
                            
                            {/* <!-- Tag filters --> */}
                            {activeFilters.length > 0 ? (
                                <div id="tag-filters" className="mt-3 flex-wrap gap-2 hidden">{/*flex*/}
                                    {/* <!-- Tag filters will be added here --> */}

                                    {activeFilters.map((tag, idx) => (
                                        <div key={idx} className="tag bg-gray-900 text-white px-3 py-1 rounded-full flex items-center gap-1">
                                        <span>{tag}</span>
                                        <button
                                            onClick={() => {
                                            setActiveFilters(activeFilters.filter((t) => t !== tag));
                                            }}
                                            className="text-white hover:text-gray-300"
                                        >
                                            ✕
                                        </button>
                                        </div>
                                    ))}
                                    <button
                                        onClick={() => setActiveFilters([])}
                                        className="text-sm text-gray-600 hover:text-gray-900 ml-2"
                                    >
                                        Clear all
                                    </button>
                                </div>
                            ) : (
                                <div className="text-sm text-gray-500">No active filters</div>
                            )}                            
                        </div>
                        
                        {/* <!-- Records List --> */}
                        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                            <div className="overflow-x-auto">
                                {records.length === 0 && (
                                    <div className="p-8 text-center">
                                        <div className="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                                        {/* svg */} 
                                        </div>
                                        <h3 className="text-lg font-medium text-gray-700">No training records found</h3>
                                        <p className="text-gray-500 mt-1">Add a new workout or change your search criteria</p>
                                    </div>
                                )}
                                <table className="w-full">
                                    <thead className="bg-gray-100 text-left">
                                        <tr>
                                            <th className="px-4 py-3 text-gray-600 font-medium">Workout</th>
                                            <th className="px-4 py-3 text-gray-600 font-medium">Date</th>
                                            <th className="px-4 py-3 text-gray-600 font-medium">Tags</th>
                                            <th className="px-4 py-3 text-gray-600 font-medium">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-200">
                                        {/* <!-- Records will be added here --> */}
                                        {records
                                            .filter((record) =>
                                            record.title.toLowerCase().includes(searchTerm.toLowerCase())
                                            )
                                            .map((record) => (
                                            <tr key={record.sessionId} className="hover:bg-gray-50" onClick={() => handleViewRecord(record.sessionId)}>
                                                <td className="px-4 py-3">
                                                <div className="font-medium text-gray-800">{record.title}</div>
                                                </td>
                                                <td className="px-4 py-3 text-gray-600">{record.date}</td>
                                                <td className="px-4 py-3">
                                                <div className="flex flex-wrap gap-1">
                                                    {record.tags.map((tag, idx) => (
                                                    <span
                                                        key={idx}
                                                        className="tag text-xs px-2 py-1 bg-gray-100 text-gray-800 rounded-full"
                                                    >
                                                        {tag}
                                                    </span>
                                                    ))}
                                                </div>
                                                </td>
                                                <td className="px-4 py-3">
                                                <div className="flex gap-2">
                                                    {/* View */}
                                                    <button
                                                    onClick={() => {
                                                        handleViewRecord(record.sessionId)
                                                    }}
                                                    className="view-btn text-gray-600 hover:text-gray-900" data-id="${record.sessionId}">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                            <circle cx="12" cy="12" r="3"></circle>
                                                        </svg>
                                                    </button>
                                                    {/* Edit */}
                                                    <button
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        handleEdit(record.sessionId);
                                                    }}
                                                    className="edit-btn text-blue-600 hover:text-blue-800" data-id="${record.sessionId}">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                        </svg>
                                                    </button>
                                                    {/* Delete */}
                                                    <button
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            setDeleteId(record.sessionId);
                                                        }}
                                                        className="delete-btn text-red-600 hover:text-red-800" data-id="${record.sessionId}">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                <polyline points="3 6 5 6 21 6"></polyline>
                                                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                            </svg>
                                                    </button>
                                                </div>
                                                </td>
                                            </tr>
                                            ))}
                                    </tbody>
                                </table>

                            </div>
                            
                            {/* <!-- Empty state --> */}
                            <div id="empty-state" className="hidden p-8 text-center">
                                <div className="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-gray-400">
                                        <path d="M18 20v-3"></path>
                                        <path d="M18 14v-3"></path>
                                        <path d="M18 8V5"></path>
                                        <path d="M6 20v-3"></path>
                                        <path d="M6 14v-3"></path>
                                        <path d="M6 8V5"></path>
                                        <rect x="2" y="2" width="8" height="20" rx="2"></rect>
                                        <rect x="14" y="2" width="8" height="20" rx="2"></rect>
                                    </svg>
                                </div>
                                <h3 className="text-lg font-medium text-gray-700">No training records found</h3>
                                <p className="text-gray-500 mt-1">Add a new workout or change your search criteria</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        {/* 卡片編輯modal */}
        {isRecordModalOpen && (
            <div
                className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                onClick={(e) => {
                    e.stopPropagation();
                    if (e.target === e.currentTarget) {
                        setIsRecordModalOpen(null);  // 點背景關閉
                    }
                }}
            >
                <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg max-h-[80vh]">
                    <h2 className="text-lg font-bold mb-4">
                        {currentRecord?.id === null ? '新增訓練紀錄' : '編輯訓練紀錄'}
                    </h2>

                    {/* 計畫名稱、設定日期編輯 */}
                    <div className="mb-4 grid grid-cols-2 gap-4">
                    {/* 計劃名稱欄位 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">計劃名稱</label>
                        <input
                        type="text"
                        value={currentRecord.title}
                        onChange={(e) => setCurrentRecord({ ...currentRecord, title: e.target.value })}
                        className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                        />
                    </div>

                    {/* 日期欄位 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1 ml-1">日期</label>
                        <input
                        type="date"
                        value={currentRecord.date}
                        onChange={(e) => setCurrentRecord({ ...currentRecord, date: e.target.value })}
                        className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-neutral-700"
                        />
                    </div>
                    </div>

                    {/* 訓練項目編輯 */}
                    <label className="block text-sm font-medium text-gray-700 mb-1">訓練項目</label>
                    <div className="mb-4 max-h-56 overflow-y-auto pr-1">
                        {currentRecord.exercises.map((exercise, index) => (
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
                            {currentRecord.exercises.filter(ex => ex.mainTag).map((exercise, index) => (
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
                            onClick={() => setCurrentRecord(null)}
                        >
                            取消
                        </button>
                        <button
                        className="px-4 py-2 flex justify-end rounded bg-neutral-800 text-white hover:bg-neutral-600 transition"
                        onClick={handleSavePlan}
                        >
                        {currentRecord?.id === null ? '新增' : '儲存修改'}
                        </button>
                    </div>
                </div>
            </div>
        )}

        {/* <!-- Record Detail Modal --> flex*/}
        {isDetailModalOpen && (
            <div 
                id="detail-modal" 
                className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-40"
                onClick={(e) => {
                    if(e.target === e.currentTarget){
                        setIsDetailModalOpen(null);
                    }
                }}
            > 
                <div className="bg-gray-50 rounded-lg w-full max-w-3xl max-h-[90vh] overflow-y-auto scrollbar-thin scrollbar-thumb-neutral-400 scrollbar-track-neutral-100 ">
                    <div id="detail-header" className="bg-gray-900 text-white p-5 ">
                        {/* detail-title & detail-date */}
                        <h2 className="text-2xl font-bold">{detailRecord.title}</h2>
                        <p className="text-gray-300">{detailRecord.date}</p>
                    </div>
                
                    <div className="p-6">
                        {/* <!-- Overview Section --> */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                            <div className="bg-gray-200 rounded-lg p-4 flex flex-col items-center justify-center">
                                <div className="text-gray-500 mb-1">Exercises</div>
                                <div id="detail-exercise-count" className="text-2xl font-bold text-gray-900">
                                    {detailRecord?.exercises?.length ?? 0}
                                </div>
                            </div>
                        </div>
                        
                        {/* <!-- Tags Section --> */}
                        <div className="mb-6">
                            <h3 className="text-lg font-medium text-gray-900 mb-2">Tags</h3>
                            <div id="detail-tags" className="flex flex-wrap gap-2">
                                {/* <!-- Tags will be added here --> */}
                                {detailRecord.exercises.map((exercise, idx) => (
                                <span
                                    key={idx}
                                    className="tag px-2 py-1 bg-gray-200 text-gray-800 rounded-full text-sm"
                                >
                                    {exercise.mainTag}
                                </span>
                                ))}
                            </div>
                        </div>
                    
                        {/* <!-- Exercises Section --> */}
                        <div>
                            <h3 className="text-lg font-medium text-gray-900 mb-2">Exercises</h3>
                            <div id="detail-exercises" className="space-y-3">
                                {/* <!-- Exercises will be added here --> */}
                                {detailRecord?.exercises?.length > 0 ? (
                                    detailRecord.exercises.map((exercise, idx) => (
                                    <div key={idx} className="bg-gray-100 rounded-lg p-3">
                                        <div className="flex justify-between items-center">
                                        <div className="font-medium">{exercise.typeName}</div>
                                        <div className="text-gray-500 text-sm">{exercise.sets.length} sets</div>
                                        </div>
                                            <div 
                                                className="flex flex-wrap gap-3 pt-1" >
                                                    {exercise.sets.map((set, idx) => (
                                                        <div key={idx} className="bg-gray-200 p-2 rounded text-center basis-[18%]">
                                                        <div className="text-xs text-gray-500">組數 {idx + 1}</div>
                                                        <div>{set.weight}kg × {set.reps}</div>
                                                        </div>
                                                    ))}
                                            </div>
                                    </div>
                                    ))
                                ) : (
                                    <div className="text-gray-500">No exercises recorded</div>
                                )}
                            </div>
                        </div>
                    
                        {/* <!-- Action Buttons --> */}
                        <div className="flex justify-end gap-2 mt-8">
                            <button
                                id="close-detail-btn" 
                                onClick={() => setIsDetailModalOpen(false)}
                                className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition"
                            >
                                Close
                            </button>
                            <button id="edit-from-detail-btn" className="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition"
                                    onClick={() => handleEdit(detailRecord.id)}
                            >
                                Edit Record
                            </button>
                        </div>
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
                            onClick={() => handleDeleteRecord(deleteId)}
                        >
                            確認刪除
                        </button>
                    </div>
                </div>
            </div>
        )}

        {/* <!-- Tag Filter Modal --> */}
        {isTagFilterModalOpen && (
        <div id="tag-filter-modal" className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
            onClick={(e) => {
                if(e.target === e.currentTarget){
                    setIsTagFilterModalOpen(false);
                }
            }}
        >{/*flex*/}
            <div className="bg-white rounded-lg p-6 w-full max-w-md">
                <h3 className="text-xl font-bold mb-4">Filter by Tags</h3>
                
                <div className="mb-4">
                    <div id="all-tags-container" className="flex flex-wrap gap-2">
                        {/* <!-- All available tags will be added here --> */}
                        {Array.from(new Set(records.flatMap((record) => record.tags || []))).map(
                            (tag, idx) => (
                            <button
                                key={idx}
                                onClick={() => {
                                setActiveFilters((prev) => {
                                    const newSet = new Set(prev);
                                    if (newSet.has(tag)) {
                                    newSet.delete(tag);
                                    } else {
                                    newSet.add(tag);
                                    }
                                    return newSet;
                                });
                                }}
                                className={`tag px-3 py-1 rounded-full ${
                                activeFilters.has(tag)
                                    ? 'bg-gray-900 text-white'
                                    : 'bg-gray-200 text-gray-700'
                                }`}
                            >
                                {tag}
                            </button>
                            )
                        )}
                    </div>
                </div>
                
                <div className="flex justify-end gap-2 mt-6">
                    <button onClick={() => setActiveFilters(new Set())}
                            id="clear-filters-btn" className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition">
                        Clear All
                    </button>
                    <button onClick={() => {setIsTagFilterModalOpen(false);}}
                            id="apply-filters-btn" className="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition">
                        Apply
                    </button>
                </div>
            </div>
        </div>
        )}
    </div>
    );
}

export default WorkoutRecordPage;
