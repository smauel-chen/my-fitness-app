import './WorkoutRecordPage.css'

import { useState } from 'react';
import './WorkoutRecordPage.css';

function WorkoutRecordPage() {
    // 👉 紀錄資料
    const [records, setRecords] = useState([
        { 
            id: 1, 
            title: "Chest & Triceps", 
            date: "2023-06-15", 
            notes: "Great session today. Increased bench press weight by 5kg. Feeling stronger!",
            tags: ["strength", "upper body"],
            exercises: [
                { name: "Bench Press", sets: 4, reps: "8-10", weight: "80kg" },
                { name: "Incline Dumbbell Press", sets: 3, reps: "10-12", weight: "25kg" },
                { name: "Tricep Pushdowns", sets: 3, reps: "12-15", weight: "35kg" },
                { name: "Dips", sets: 3, reps: "10", weight: "Bodyweight" }
            ]
        },
        { 
            id: 2, 
            title: "HIIT Cardio", 
            date: "2023-06-17", 
            notes: "Intense session. Kept heart rate above 150bpm for most of the workout.",
            tags: ["cardio", "intense"],
            exercises: [
                { name: "Burpees", sets: 4, reps: "20", weight: "Bodyweight" },
                { name: "Mountain Climbers", sets: 4, reps: "30 sec", weight: "Bodyweight" },
                { name: "Jump Squats", sets: 4, reps: "15", weight: "Bodyweight" },
                { name: "High Knees", sets: 4, reps: "30 sec", weight: "Bodyweight" }
            ]
        },
        { 
            id: 3, 
            title: "Leg Day", 
            date: "2023-06-19", 
            notes: "Focused on form today. Legs are definitely going to be sore tomorrow!",
            tags: ["strength", "lower body"],
            exercises: [
                { name: "Squats", sets: 4, reps: "8-10", weight: "100kg" },
                { name: "Romanian Deadlifts", sets: 3, reps: "10-12", weight: "80kg" },
                { name: "Leg Press", sets: 3, reps: "12-15", weight: "150kg" },
                { name: "Calf Raises", sets: 4, reps: "15-20", weight: "60kg" }
            ]
        },
    ]);

    // 👉 搜尋欄位
    const [searchTerm, setSearchTerm] = useState('');

    // 👉 是否顯示 Detail Modal
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

    // 👉 當前要編輯/查看的紀錄
    const [currentRecord, setCurrentRecord] = useState(null);

    // 👉 篩選器的 tag 狀態
    const [activeFilters, setActiveFilters] = useState(new Set());


    // 👉 用於 tag filter modal
    const [isTagFilterModalOpen, setIsTagFilterModalOpen] = useState(false);

    // Modal 開關
    const [isRecordModalOpen, setIsRecordModalOpen] = useState(false);

    // 當前表單的狀態
    const [currentRecordId, setCurrentRecordId] = useState(null);
    const [recordTitle, setRecordTitle] = useState('');
    const [recordDate, setRecordDate] = useState('');
    const [recordNotes, setRecordNotes] = useState('');
    const [currentRecordTags, setCurrentRecordTags] = useState([]);
    const [currentRecordExercises, setCurrentRecordExercises] = useState([]);

    // tag input
    const [newTag, setNewTag] = useState('');

    // 詳情 Modal 控制
    const [detailRecord, setDetailRecord] = useState(null);

    // 根據搜尋與篩選條件產生的列表
    const filteredRecords = records.filter((record) => {
        const matchesSearch =
          searchTerm === '' ||
          record.title.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesTags =
          activeFilters.size === 0 ||
          record.tags.some((tag) => activeFilters.has(tag));
        return matchesSearch && matchesTags;
    });

    const handleRemoveFilter = (tagToRemove) => {
        setActiveFilters((prev) => prev.filter((tag) => tag !== tagToRemove));
      };
      
      const handleClearAllFilters = () => {
        setActiveFilters([]);
      };
      




return (
<div>
    <div >
        {/* <!-- Main Content --> */}
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
                    {/* <!-- Header -->
                    <div className="mb-6">
                        <h1 className="text-2xl font-bold text-gray-900">訓練紀錄</h1>
                    </div> */}

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
                                <button onClick={() => {//add-record-btn
                                            setCurrentRecord(null);
                                            setRecordTitle('');
                                            setRecordDate(new Date().toISOString().split('T')[0]);  // 預設今天
                                            setRecordNotes('');
                                            setCurrentRecordTags([]);
                                            setCurrentRecordExercises([]);
                                            setIsRecordModalOpen(true);
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
                                        <tr key={record.id} className="hover:bg-gray-50">
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
                                                <button
                                                onClick={() => {
                                                    setCurrentRecord(record);
                                                    setIsDetailModalOpen(true);
                                                }}
                                                className="view-btn text-gray-600 hover:text-gray-900" data-id="${record.id}">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                        <circle cx="12" cy="12" r="3"></circle>
                                                    </svg>
                                                View
                                                </button>
                                                <button
                                                onClick={() => {
                                                    setCurrentRecord(record);
                                                    setCurrentRecordId(record.id);
                                                    setRecordTitle(record.title);
                                                    setRecordDate(record.date);
                                                    setRecordNotes(record.notes || '');
                                                    setCurrentRecordTags([...record.tags]);
                                                    setCurrentRecordExercises([...record.exercises]);
                                                    setIsRecordModalOpen(true);
                                                }}
                                                className="edit-btn text-blue-600 hover:text-blue-800" data-id="${record.id}">

                                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                    </svg>
                                                Edit
                                                </button>
                                                <button
                                                    onClick={() => {
                                                        if (window.confirm('Delete this record?')) {
                                                        setRecords(records.filter((r) => r.id !== record.id));
                                                        }
                                                    }}
                                                    className="delete-btn text-red-600 hover:text-red-800" data-id="${record.id}">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <polyline points="3 6 5 6 21 6"></polyline>
                                                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                        </svg>
                                                Delete
                                                </button>
                                            </div>
                                            </td>
                                        </tr>
                                        ))}
                                </tbody>
                                {records.length === 0 && (
                                <div className="p-8 text-center">
                                    <div className="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                                    {/* svg */} 
                                    </div>
                                    <h3 className="text-lg font-medium text-gray-700">No training records found</h3>
                                    <p className="text-gray-500 mt-1">Add a new workout or change your search criteria</p>
                                </div>
                                )}
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
    
    {/* <!-- Add/Edit Record Modal --> */}
    {isRecordModalOpen &&(
    <div id="record-modal" className="fixed inset-0 bg-black bg-opacity-50 items-center justify-center z-50 hidden"
         onClick={(e) => {
            if(e.target.value === e.currentRecord.target){
                setIsRecordModalOpen(false);
            }
        }}
    >{/*flex*/}
        <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
            <h3 id="modal-title" className="text-xl font-bold mb-4">Add New Training Record</h3>
            
            <div className="mb-4">
                <label className="block text-gray-700 mb-2">Workout Name</label>
                {/* <input id="record-title" type="text" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"/> */}
                <input
                type="text"
                value={recordTitle}
                onChange={(e) => setRecordTitle(e.target.value)}
                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                />
            </div>
            
            <div className="mb-4">
                <label className="block text-gray-700 mb-2">Date</label>
                {/* <input id="record-date" type="date" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"/> */}
                <input
                type="date"
                value={recordDate}
                onChange={(e) => setRecordDate(e.target.value)}
                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                />
            </div>
            
            <div className="mb-4">
                <label className="block text-gray-700 mb-2">Notes</label>
                {/* <textarea id="record-notes" rows="3" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"></textarea> */}
                <textarea
                rows="3"
                value={recordNotes}
                onChange={(e) => setRecordNotes(e.target.value)}                                         
                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                />
            </div>
            
            <div className="mb-4">
                <label className="block text-gray-700 mb-2">Exercises</label>
                <div className="space-y-3">
                    {/* <!-- Exercises will be added here --> id="exercises-container"*/}
                    {currentRecordExercises.map((exercise, idx) => (
                        <div key={idx} className="bg-gray-50 rounded-lg p-3">
                            <div className="flex justify-between items-center mb-2">
                            <div className="font-medium">{exercise.name}</div>
                            <button
                                onClick={() => {
                                const newExercises = [...currentRecordExercises];
                                newExercises.splice(idx, 1);
                                setCurrentRecordExercises(newExercises);
                                }}
                                className="text-gray-600 hover:text-gray-900"
                            >
                                ✕
                            </button>
                            </div>
                            <div className="grid grid-cols-3 gap-2">
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">Sets</label>
                                <input
                                type="number"
                                min="1"
                                value={exercise.sets}
                                onChange={(e) => {
                                    const newExercises = [...currentRecordExercises];
                                    newExercises[idx].sets = parseInt(e.target.value) || 1;
                                    setCurrentRecordExercises(newExercises);
                                }}
                                className="w-full px-2 py-1 text-sm rounded border border-gray-300"
                                />
                            </div>
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">Reps</label>
                                <input
                                type="text"
                                value={exercise.reps}
                                onChange={(e) => {
                                    const newExercises = [...currentRecordExercises];
                                    newExercises[idx].reps = e.target.value;
                                    setCurrentRecordExercises(newExercises);
                                }}
                                className="w-full px-2 py-1 text-sm rounded border border-gray-300"
                                />
                            </div>
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">Weight</label>
                                <input
                                type="text"
                                value={exercise.weight}
                                onChange={(e) => {
                                    const newExercises = [...currentRecordExercises];
                                    newExercises[idx].weight = e.target.value;
                                    setCurrentRecordExercises(newExercises);
                                }}
                                className="w-full px-2 py-1 text-sm rounded border border-gray-300"
                                />
                            </div>
                            </div>
                        </div>
                    ))}
                </div>
                <button
                    onClick={() => {
                        setCurrentRecordExercises([
                        ...currentRecordExercises,
                        {
                            name: 'New Exercise',
                            sets: 3,
                            reps: '10',
                            weight: 'Bodyweight',
                        },
                        ]);
                    }}
                    className="mt-2 text-gray-600 hover:text-gray-900 flex items-center gap-1"
                    >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="16"></line>
                        <line x1="8" y1="12" x2="16" y2="12"></line>
                    </svg>
                    Add Exercise
                </button>
            </div>
            
            <div className="mb-4">
                <label className="block text-gray-700 mb-2">Tags</label>
                <div className="flex flex-wrap gap-2 mb-2">
                    {/* <!-- Tags will be added here -->  id="record-tags-container"*/}
                    {currentRecordTags.map((tag, idx) => (
                        <div
                        key={idx}
                        className="tag bg-gray-100 text-gray-800 px-3 py-1 rounded-full flex items-center gap-1"
                        >
                        <span>{tag}</span>
                        <button
                            onClick={() => {
                            setCurrentRecordTags(currentRecordTags.filter((t) => t !== tag));
                            }}
                            className="text-gray-600 hover:text-gray-900"
                        >
                            ✕
                        </button>
                        </div>
                    ))}
                </div>
                <div className="flex gap-2">
                    <input
                        type="text"
                        placeholder="Add a tag"
                        value={newTag}
                        onChange={(e) => setNewTag(e.target.value)}
                        className="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                    />
                    <button
                        onClick={() => {
                        const trimmed = newTag.trim().toLowerCase();
                        if (trimmed && !currentRecordTags.includes(trimmed)) {
                            setCurrentRecordTags([...currentRecordTags, trimmed]);
                        }
                        setNewTag('');
                        }}
                        className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition"
                    >
                        Add
                    </button>
                </div>
            </div>
            
            <div className="flex justify-end gap-2 mt-6">
                <button id="cancel-btn" className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition">
                    Cancel
                </button>
                <button
                    onClick={() => {//save-btn
                        if (!recordTitle.trim() || !recordDate) {
                        alert('Please fill in all required fields.');
                        return;
                        }
                        if (currentRecordId === null) {
                        // 新增
                        const newId = records.length > 0 ? Math.max(...records.map((r) => r.id)) + 1 : 1;
                        setRecords([
                            ...records,
                            {
                            id: newId,
                            title: recordTitle,
                            date: recordDate,
                            notes: recordNotes,
                            tags: currentRecordTags,
                            exercises: currentRecordExercises,
                            },
                        ]);
                        } else {
                        // 編輯
                        setRecords(
                            records.map((r) =>
                            r.id === currentRecordId
                                ? {
                                    ...r,
                                    title: recordTitle,
                                    date: recordDate,
                                    notes: recordNotes,
                                    tags: currentRecordTags,
                                    exercises: currentRecordExercises,
                                }
                                : r
                            )
                        );
                        }
                        setIsRecordModalOpen(false);
                    }}
                    className="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition"
                    >
                    Save
                </button>
            </div>
        </div>
    </div>
    )}
    
    {/* <!-- Record Detail Modal --> flex*/}
    {isDetailModalOpen && (
        <div id="detail-modal" className="fixed inset-0 bg-black bg-opacity-50 items-center justify-center z-50 hidden">        <div className="bg-white rounded-lg w-full max-w-3xl max-h-[90vh] overflow-y-auto">
            <div id="detail-header" className="bg-gray-900 text-white p-5 rounded-t-lg">
                {/* detail-title & detail-date */}
                <h2 className="text-2xl font-bold">{detailRecord?.title}</h2>
                <p className="text-gray-300">
                {detailRecord ? new Date(detailRecord.date).toLocaleDateString() : ''}
                </p>
            </div>
            
            <div className="p-6">
                {/* <!-- Overview Section --> */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                    <div className="bg-gray-100 rounded-lg p-4 flex flex-col items-center justify-center">
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
                        {detailRecord?.tags?.length > 0 ? (
                            detailRecord.tags.map((tag, idx) => (
                            <span
                                key={idx}
                                className="tag px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-sm"
                            >
                                {tag}
                            </span>
                            ))
                        ) : (
                            <span className="text-gray-500">No tags</span>
                        )}
                    </div>
                </div>
                
                {/* <!-- Notes Section --> */}
                <div className="mb-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-2">Notes</h3>
                    <div id="detail-notes" className="bg-gray-50 rounded-lg p-4 text-gray-700">
                        {detailRecord?.notes || 'No notes for this workout.'}
                    </div>
                </div>
                
                {/* <!-- Exercises Section --> */}
                <div>
                    <h3 className="text-lg font-medium text-gray-900 mb-2">Exercises</h3>
                    <div id="detail-exercises" className="space-y-3">
                        {/* <!-- Exercises will be added here --> */}
                        {detailRecord?.exercises?.length > 0 ? (
                            detailRecord.exercises.map((exercise, idx) => (
                            <div key={idx} className="bg-gray-50 rounded-lg p-3">
                                <div className="flex justify-between items-center">
                                <div className="font-medium">{exercise.name}</div>
                                <div className="text-gray-500 text-sm">{exercise.sets} sets</div>
                                </div>
                                <div className="flex justify-between text-sm text-gray-600 mt-1">
                                <div>Reps: {exercise.reps}</div>
                                <div>Weight: {exercise.weight}</div>
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
                            onClick={() => {
                                // 快速帶入編輯
                                setCurrentRecordId(detailRecord.id);
                                setRecordTitle(detailRecord.title);
                                setRecordDate(detailRecord.date);
                                setRecordNotes(detailRecord.notes || '');
                                setCurrentRecordTags([...detailRecord.tags]);
                                setCurrentRecordExercises([...detailRecord.exercises]);
                                setIsDetailModalOpen(false);
                                setIsRecordModalOpen(true);
                              }}
                    >
                        Edit Record
                    </button>
                </div>
            </div>
        </div>
    </div>
    )}
    
    {/* <!-- Tag Filter Modal --> */}
    {isTagFilterModalOpen && (
    <div id="tag-filter-modal" className="fixed inset-0 bg-black bg-opacity-50  items-center justify-center z-50 hidden"
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
                      {Array.from(new Set(records.flatMap((record) => record.tags))).map(
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
                <button onClick={() => setIsTagFilterModalOpen(false)}
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
