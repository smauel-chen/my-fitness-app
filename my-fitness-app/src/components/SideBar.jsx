// Sidebar.jsx
import { Link, useLocation } from 'react-router-dom';

function Sidebar({userId}) {
    const location = useLocation(); // 用來判斷哪個頁面高亮

    return (
        <aside id="sidebar" className=" sidebar w-64 top-0 left-0 h-screen fixed z-40 overflow-y-auto">
            <div className="p-5">
                <div className="flex items-center space-x-2 mb-8">
                    <svg className="w-8 h-8 text-accent" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20.24 12.24a6 6 0 0 0-8.49-8.49L9 6l1.5 1.5L12 6l-1.5-1.5a3 3 0 1 1 4.24 4.24L12 12l-1.5-1.5L9 12l2.25 2.25a6 6 0 0 0 8.49-8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M3.76 12.24a6 6 0 0 0 8.49 8.49L15 18l-1.5-1.5L12 18l1.5 1.5a3 3 0 1 1-4.24-4.24L12 12l1.5 1.5L15 12l-2.25-2.25a6 6 0 0 0-8.49 8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    <span className="text-xl font-bold text-white">Lift Log</span>
                </div>
                
                <div className="mb-8">
                    <div className="flex items-center space-x-3 mb-6">
                        <div className="w-10 h-10 rounded-full bg-accent-light flex items-center justify-center">
                            <span className="text-accent font-medium">JL</span>
                        </div>
                        <div>
                            <div className="text-white font-medium">使用者</div>
                            <div className="text-gray-400 text-sm">個性簽名</div>
                        </div>
                    </div>
                </div>
                
                <nav>
                    <ul className="space-y-2">
                        <li>
                            <Link   to="/dashboard" 
                                    className={`nav-link flex items-center space-x-3 px-4 py-2 rounded-md text-gray-200 hover:text-white hover:bg-neutral-700 ${
                                        location.pathname === '/dashboard' ? ' bg-neutral-600 ' : ''
                                    }`}
                            >
                                    {/* </li>className=" active flex items-center space-x-3 px-4 py-3 "> */}
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
                                </svg>
                                <span>儀表板</span>
                            </Link>
                        </li>
                        <li>
                            <Link   to="/dashboard/page" 
                                    className={`nav-link flex items-center space-x-3 px-4 py-2 rounded-md text-gray-200 hover:text-white hover:bg-neutral-700
                                    ${location.pathname === '/dashboard/page' ? ' bg-neutral-600':''}`}>
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                                </svg>
                                <span>訓練記錄</span>
                            </Link>
                        </li>
                        <li>
                            <Link   to="/dashboard/types" 
                                    className={`nav-link flex items-center space-x-3 px-4 py-2 rounded-md text-gray-200 hover:text-white hover:bg-neutral-700
                                        ${location.pathname === '/dashboard/types' ? ' bg-neutral-600':''}`}>                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                                </svg>
                                <span>動作資料庫</span>
                            </Link>
                        </li>
                        <li>
                            <Link   to="/dashboard/charts"
                                    className={`nav-link flex items-center space-x-3 px-4 py-2 rounded-md text-gray-200 hover:text-white hover:bg-neutral-700
                                        ${location.pathname === '/dashboard/charts' ? ' bg-neutral-600':''}`}>
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                                </svg>
                                <span>統計圖表</span>
                            </Link>
                        </li>
                        <li>
                            <Link    to="/dashboard/demo" 
                                    className={`nav-link flex items-center space-x-3 px-4 py-2 rounded-md text-gray-200 hover:text-white hover:bg-neutral-700
                                        ${location.pathname === '/dashboard/demo' ? ' bg-neutral-600':''}`}>                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                                </svg>
                                <span>個人檔案</span>
                            </Link>
                        </li>
                    </ul>
                </nav>
            </div>

            <div className="absolute bottom-0 left-0 right-0 p-5">
                <button
                    onClick={() => {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userId');
                        window.location.href = '/';
                    }}
                    className="flex items-center space-x-3 px-4 py-3 rounded-md text-gray-200 hover:text-white">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                    </svg>
                    <span>登出</span>
                </button>
            </div>
        </aside>
    );
}

export default Sidebar;
