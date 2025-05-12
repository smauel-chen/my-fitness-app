
function LoginEnrollPage() {
    return (
        <section className="py-20 bg-white">
        <div className="container mx-auto px-4">
            <div className="max-w-4xl mx-auto">
                <div className="text-center mb-10">
                    <h2 className="text-3xl font-bold mb-4">開始你的健身紀錄之旅</h2>
                    <p className="text-gray-600">立即註冊或登入，開始追蹤你的健身進度。</p>
                </div>
                
                <div className="flex flex-col md:flex-row gap-8">
                    {/* <!-- 登入表單 --> */}
                    <div className="md:w-1/2 bg-gray-50 rounded-lg p-8">
                        <h3 className="text-xl font-bold mb-6">登入</h3>
                        <form>
                            <div className="mb-4">
                                <label className="block text-sm font-medium text-gray-700 mb-1">電子郵件</label>
                                <input type="email" className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-200" placeholder="your@email.com" />
                            </div>
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-700 mb-1">密碼</label>
                                <input type="password" className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-200" placeholder="••••••••" />
                            </div>
                            <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center">
                                    <input type="checkbox" id="remember" className="h-4 w-4 border-gray-300 rounded" />
                                    <label htmlFor="remember" className="ml-2 text-sm text-gray-600">記住我</label>
                                </div>
                                <a href="#" className="text-sm text-gray-600 hover:text-black">忘記密碼？</a>
                            </div>
                            <button type="submit" className="w-full py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors"
                                    onClick={() => navigate('/login')}>
                                登入
                            </button>
                        </form>
                    </div>
                    
                    {/* <!-- 註冊表單 --> */}
                    <div className="md:w-1/2 bg-gray-50 rounded-lg p-8">
                        <h3 className="text-xl font-bold mb-6">註冊</h3>
                        <form>
                            <div className="mb-4">
                                <label className="block text-sm font-medium text-gray-700 mb-1">姓名</label>
                                <input type="text" className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-200" placeholder="你的名字" />
                            </div>
                            <div className="mb-4">
                                <label className="block text-sm font-medium text-gray-700 mb-1">電子郵件</label>
                                <input type="email" className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-200" placeholder="your@email.com" />
                            </div>
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-700 mb-1">密碼</label>
                                <input type="password" className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-200" placeholder="至少 8 個字元" />
                            </div>
                            <div className="flex items-center mb-6">
                                <input type="checkbox" id="terms" className="h-4 w-4 border-gray-300 rounded" />
                                <label htmlFor="terms" className="ml-2 text-sm text-gray-600">我同意<a href="#" className="text-black hover:underline">服務條款</a>和<a href="#" className="text-black hover:underline">隱私政策</a></label>
                            </div>
                            {/* type="submit" */}
                            <button className="w-full py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors"
                                    onClick={() => navigate("/register")}>
                                註冊
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        </section>
    );
}

export default LoginEnrollPage;