import { useState } from "react";
import './WorkoutTypePage.css'

function WorkoutTypePage() {
    
    const [isFilterOpen, setIsFilterOpen] = useState(false);
    const [viewType, setViewType] = useState('grid'); 
    const [activeTag, setActiveTag] = useState('All');

    const tagList = ['All', 'Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core', 'Cardio'];
    
    const [searchTerm, setSearchTerm] = useState('');

    const workouts = [
        {
            id: 1,
            name: 'Barbell Squat',
            tags: ['Legs', 'Compound', 'Barbell']
        },
        {
            id: 2,
            name: 'Leg Press',
            tags: ['Legs', 'Compound', 'Machine']
        },
        {
            id: 3,
            name: 'Romanian Deadlift',
            tags: ['Legs', 'Hamstrings', 'Barbell']
        },
        {
            id: 4,
            name: 'Walking Lunges',
            tags: ['Legs', 'Unilateral', 'Dumbbell']
        },
        {
            id: 5,
            name: 'Leg Press6',
            tags: ['Legs', 'Compound', 'Machine']
        },
        {
            id: 6,
            name: 'Leg Press5',
            tags: ['Legs', 'Compound', 'Dumbbell']
        },
        {
            id: 7,
            name: 'Leg Press4',
            tags: ['Legs', 'Compound', 'Machine']
        },
        {
            id: 8,
            name: 'Leg Press3',
            tags: ['Legs', 'Compound', 'Dumbbell']
        },
        {
            id: 9,
            name: 'Leg Press2',
            tags: ['Legs', 'Compound', 'Dumbbell']
        },
        {
            id: 10,
            name: 'Leg Press1',
            tags: ['Legs', 'Compound', 'Machine']
        }
        // ...其餘
    ];

    const filteredWorkouts = workouts.filter(workout => {
        const matchesSearch = workout.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesTag = activeTag === 'All' || workout.tags.includes(activeTag);
        return matchesSearch && matchesTag;
    });

    
    return (
        <div>
            <div className=" bg-gray-100">
                {/* <!-- Main Content --> */}
                <div className="flex-1 overflow-y-auto">
                <div className="bg-white subtle-shadow ">
                    <div className="container mx-auto px-6 py-4 flex justify-between items-center">
                        <div className="flex items-center">
                            <h1 className="text-2xl font-bold text-gray-900">動作資料庫</h1>
                        </div>
                        <div className="flex items-center space-x-4">
                            <button className="px-4 py-2 bg-gray-800 text-white rounded-md hover:bg-gray-700 transition-colors flex items-center">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                                新增動作
                            </button>
                            <div className="relative">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-gray-500 cursor-pointer" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                                </svg>
                                <span className="absolute top-0 right-0 h-2 w-2 bg-red-500 rounded-full"></span>
                            </div>
                        </div>
                    </div>
                </div>
                    <main className="p-6">
                        {/* <!-- Search and Filter Section --> */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6">
                                <div className="relative w-full md:w-1/2 mb-4 md:mb-0">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                        </svg>
                                    </div>
                                    <input onChange={(e) => setSearchTerm(e.target.value)} value={searchTerm} type="text" className="search-input block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:ring-0 focus:border-gray-500 transition duration-150 ease-in-out" placeholder="Search for workouts..." />
                                </div>
                                <div className="flex space-x-2">
                                    <button onClick={() => setViewType('grid')}
                                            className={`view-toggle-btn p-2 rounded-md ${viewType === 'grid' ? 'active' : ''}`}>
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                                        </svg>
                                    </button>
                                    <button onClick={() => setViewType('list')}
                                            className={`view-toggle-btn p-2 rounded-md ${viewType === 'list' ? 'active' : ''}`}>
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                                        </svg>
                                    </button>
                                    <button onClick={() => setIsFilterOpen(!isFilterOpen)}
                                            className="p-2 rounded-md border border-gray-300 hover:bg-gray-100 transition-colors">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                        </svg>
                                    </button>
                                </div>
                            </div>
                            
                            <div id="filter-panel" className={`${isFilterOpen ? '' : 'hidden'} bg-gray-50 p-4 rounded-md mb-4`}>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div>
                                        <h3 className="font-medium text-gray-700 mb-2">Equipment</h3>
                                        <div className="space-y-2">
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Barbell</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Dumbbell</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Machine</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Bodyweight</span>
                                            </label>
                                        </div>
                                    </div>
                                    <div>
                                        <h3 className="font-medium text-gray-700 mb-2">Difficulty</h3>
                                        <div className="space-y-2">
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Beginner</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Intermediate</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Advanced</span>
                                            </label>
                                        </div>
                                    </div>
                                    <div>
                                        <h3 className="font-medium text-gray-700 mb-2">Type</h3>
                                        <div className="space-y-2">
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Strength</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Cardio</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Flexibility</span>
                                            </label>
                                            <label className="flex items-center">
                                                <input type="checkbox" className="rounded text-gray-800 focus:ring-0" />
                                                <span className="ml-2 text-sm text-gray-700">Balance</span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div className="mt-4 flex justify-end">
                                    <button className="px-4 py-2 bg-gray-800 text-white rounded-md hover:bg-gray-700 transition-colors">Apply Filters</button>
                                </div>
                            </div>
                            
                            <div className="flex flex-wrap gap-2">
                                {tagList.map(tag => (
                                    <span
                                        key={tag}
                                        onClick={() => setActiveTag(tag)}
                                        className={`tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer ${activeTag === tag ? 'active' : ''}`}
                                    >
                                        {tag}
                                    </span>
                                ))}
                                {/* <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">All</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Chest</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Back</span>
                                <span className="tag active px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Legs</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Shoulders</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Arms</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Core</span>
                                <span className="tag px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm cursor-pointer">Cardio</span> */}
                            </div>
                        </div>
                        
                        {/* <!-- Workout List --> */}
                        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                            {/* <!-- List Header --> */}
                            <div className="bg-gray-50 px-6 py-3 border-b border-gray-200">
                                <div className="flex justify-between items-center">
                                    <h2 className="text-lg font-medium text-gray-800">Leg Workouts</h2>
                                    <div className="text-sm text-gray-500">12 workouts found</div>
                                </div>
                            </div>
                            
                            {/* <!-- List Items --> */}
                            <ul className="divide-y divide-gray-100">
                                {filteredWorkouts.map((workout) => (
                                    <li key={workout.id} className="workout-item p-4 animate-fade-in" style={{ animationDelay : '0.05s'}}>
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center">
                                                <div className="h-10 w-10 rounded-full icon-bg-strength flex items-center justify-center text-white mr-4">
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3" />
                                                    </svg>
                                                </div>
                                                <div>
                                                    <h3 className="font-medium text-gray-800">{workout.name}</h3>
                                                    <div className="flex flex-wrap gap-1 mt-1">
                                                        {workout.tags.map(tag => (
                                                            <span
                                                                key={tag}
                                                                className="px-2 py-0.5 bg-gray-100 text-xs text-gray-600 rounded"
                                                            >
                                                                {tag}
                                                            </span>
                                                        ))}
                                                    </div>
                                                </div>
                                            </div>
                                            <button className="px-3 py-1.5 bg-gray-800 text-white text-sm rounded hover:bg-gray-700 transition-colors">Details</button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                            
                            {/* <!-- Pagination --> */}
                            <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                                <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                                    <div>
                                        <p className="text-sm text-gray-700">
                                            Showing <span className="font-medium">1</span> to <span className="font-medium">12</span> of <span className="font-medium">12</span> results
                                        </p>
                                    </div>
                                    <div>
                                        <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                                            <a href="#" className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                                                <span className="sr-only">Previous</span>
                                                <svg className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                                    <path fillRule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clipRule="evenodd" />
                                                </svg>
                                            </a>
                                            <a href="#" className="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-gray-800 text-sm font-medium text-white hover:bg-gray-700">
                                                1
                                            </a>
                                            <a href="#" className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                                                <span className="sr-only">Next</span>
                                                <svg className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                                    <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                                                </svg>
                                            </a>
                                        </nav>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            </div>
        </div>
    );
}

export default WorkoutTypePage;
