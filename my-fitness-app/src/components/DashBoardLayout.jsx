import Sidebar from './SideBar';
import { Outlet } from 'react-router-dom';

function DashboardLayout() {
    return (
        <div className="flex min-h-screen">
            <Sidebar />
            <main className="flex-1 ml-64 overflow-y-auto">
                <div >
                    <Outlet /> {/* 這裡就是放動態切換的內容 */}
                </div>
            </main>
        </div>
    );
}

export default DashboardLayout;
