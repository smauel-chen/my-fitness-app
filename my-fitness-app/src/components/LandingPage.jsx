import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import './LandingPage.css';

function LandingPage() {

    const navigate = useNavigate(); 

    const handleSmoothScroll = (e, targetId) => {
        e.preventDefault();
        const targetElement = document.querySelector(targetId);
        window.scrollTo({
            top: targetElement.offsetTop - 80,
            behavior: 'smooth',
        });
    };

    const [activeSection, setActiveSection] = useState('');

    useEffect(() => {
        const handleScroll = () => {
            const sections = document.querySelectorAll('section, header');
            let current = '';
    
            sections.forEach(section => {
                const sectionTop = section.offsetTop;
                if (window.pageYOffset >= sectionTop - 200) {
                    current = section.getAttribute('id');
                }
            });
    
            setActiveSection(current);
        };
    
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);
        
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };
    
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

        
        // 登入和註冊按鈕
        // document.getElementById('loginBtn').addEventListener('click', () => {
        //     window.scrollTo({
        //         top: document.querySelector('section:last-of-type').offsetTop - 80,
        //         behavior: 'smooth'
        //     });
        // });
        
        // document.getElementById('registerBtn').addEventListener('click', () => {
        //     window.scrollTo({
        //         top: document.querySelector('section:last-of-type').offsetTop - 80,
        //         behavior: 'smooth'
        //     });
        // });

    return (

        <div className="bg-white text-gray-800">
            {/* <!-- 導航欄 --> */}
            <nav id="navbar" className={`fixed top-0 w-full bg-white bg-opacity-95 shadow-sm z-50 transition-all duration-300 ${isScrolled ? 'shadow' : ''}`}>
                <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                    <div className="flex items-center space-x-2">
                        <svg className="w-8 h-8" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.24 12.24a6 6 0 0 0-8.49-8.49L9 6l1.5 1.5L12 6l-1.5-1.5a3 3 0 1 1 4.24 4.24L12 12l-1.5-1.5L9 12l2.25 2.25a6 6 0 0 0 8.49-8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="M3.76 12.24a6 6 0 0 0 8.49 8.49L15 18l-1.5-1.5L12 18l1.5 1.5a3 3 0 1 1-4.24-4.24L12 12l1.5 1.5L15 12l-2.25-2.25a6 6 0 0 0-8.49 8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                        <span className="text-xl font-bold">FitTrack</span>
                    </div>
                    <div className="hidden md:flex space-x-8">
                        <a href="#intro" onClick={(e) => handleSmoothScroll(e, '#intro')} className={`nav-link px-1 py-2 hover:text-black transition-colors ${activeSection === 'intro' ? 'active' : ''}`}>介紹</a>
                        <a href="#record" onClick={(e) => handleSmoothScroll(e, '#record')} className={`nav-link px-1 py-2 hover:text-black transition-colors ${activeSection === 'record' ? 'active' : ''}`}>紀錄功能</a>
                        <a href="#stats" onClick={(e) => handleSmoothScroll(e, '#stats')} className={`nav-link px-1 py-2 hover:text-black transition-colors ${activeSection === 'stats' ? 'active' : ''}`}>統計分析</a>
                        <a href="#author" onClick={(e) => handleSmoothScroll(e, '#author')} className={`nav-link px-1 py-2 hover:text-black transition-colors ${activeSection === 'author' ? 'active' : ''}`}>關於作者</a>
                    </div>
                    <div className="flex items-center space-x-4">
                        <button 
                            onClick={() => navigate("/login")}
                            id="loginBtn" 
                            className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-100 transition-colors">     
                                登入
                        </button>
                        <button onClick={() => navigate("/register")}
                                id="registerBtn" className="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800 transition-colors">
                            註冊
                        </button>
                    </div>
                </div>
            </nav>

            {/* <!-- 英雄區塊 --> */}
            <header id="hero" className="relative h-screen bg-gray-50 flex items-center justify-center">
                <div className="container mx-auto px-4 flex flex-col md:flex-row items-center">
                    <div className="md:w-1/2 mb-10 md:mb-0">
                        <h1 className="text-4xl md:text-5xl font-bold mb-6">簡約而強大的<br/>健身紀錄工具</h1>
                        <p className="text-xl text-gray-600 mb-8">追蹤你的健身進度，分析你的表現，<br />讓數據驅動你的健身旅程。</p>
                        <div className="flex space-x-4">
                            <button className="px-6 py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors">開始使用</button>
                            <button className="px-6 py-3 border border-gray-300 rounded-md hover:bg-gray-100 transition-colors">了解更多</button>
                        </div>
                    </div>
                    <div className="md:w-1/2 relative">
                        <div className="relative bg-white rounded-lg shadow-xl p-6 md:p-8 mx-auto max-w-md">
                            <div className="flex justify-between items-center mb-6">
                                <h3 className="text-lg font-medium">今日訓練</h3>
                                <span className="text-sm text-gray-500">2023/10/25</span>
                            </div>
                            <div className="space-y-4">
                                <div className="p-4 bg-gray-50 rounded-md">
                                    <div className="flex justify-between mb-2">
                                        <span className="font-medium">啞鈴臥推</span>
                                        <span className="text-sm text-gray-500">胸部</span>
                                    </div>
                                    <div className="grid grid-cols-3 gap-2 text-sm">
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 1</div>
                                            <div>20kg × 12</div>
                                        </div>
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 2</div>
                                            <div>25kg × 10</div>
                                        </div>
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 3</div>
                                            <div>25kg × 8</div>
                                        </div>
                                    </div>
                                </div>
                                <div className="p-4 bg-gray-50 rounded-md">
                                    <div className="flex justify-between mb-2">
                                        <span className="font-medium">槓鈴深蹲</span>
                                        <span className="text-sm text-gray-500">腿部</span>
                                    </div>
                                    <div className="grid grid-cols-3 gap-2 text-sm">
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 1</div>
                                            <div>40kg × 10</div>
                                        </div>
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 2</div>
                                            <div>45kg × 8</div>
                                        </div>
                                        <div className="bg-gray-100 p-2 rounded">
                                            <div className="text-xs text-gray-500">組數 3</div>
                                            <div>50kg × 6</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button className="w-full mt-6 py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors">
                                新增訓練
                            </button>
                        </div>
                        <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-gray-200 rounded-full -z-10"></div>
                        <div className="absolute -top-4 -left-4 w-16 h-16 bg-gray-300 rounded-full -z-10"></div>
                    </div>
                </div>
                <div className="scroll-indicator">
                    <svg className="w-6 h-6 animate-bounce" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"></path>
                    </svg>
                </div>
            </header>

            {/* <!-- 介紹區塊 --> */}
            <section id="intro" className="py-20 bg-white">
                <div className="container mx-auto px-4">
                    <div className="text-center mb-16">
                        <h2 className="text-3xl font-bold mb-4">為什麼選擇 FitTrack？</h2>
                        <p className="text-gray-600 max-w-2xl mx-auto">我們提供簡約而強大的健身紀錄工具，幫助你追蹤進度、分析表現，讓你的健身旅程更有效率。</p>
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <div className="p-6 bg-gray-50 rounded-lg">
                            <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center mb-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold mb-2">簡單記錄</h3>
                            <p className="text-gray-600">快速記錄你的每次訓練，包括重量、次數和組數，無需複雜操作。</p>
                        </div>
                        
                        <div className="p-6 bg-gray-50 rounded-lg">
                            <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center mb-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold mb-2">數據分析</h3>
                            <p className="text-gray-600">透過視覺化圖表，清晰了解你的進步和表現趨勢。</p>
                        </div>
                        
                        <div className="p-6 bg-gray-50 rounded-lg">
                            <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center mb-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold mb-2">訓練提醒</h3>
                            <p className="text-gray-600">設定訓練計劃和提醒，確保你不會錯過任何一次訓練。</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* <!-- 紀錄功能區塊 --> */}
            <section id="record" className="py-20 bg-gray-50 section-transition">
                <div className="container mx-auto px-4">
                    <div className="flex flex-col md:flex-row items-center">
                        <div className="md:w-1/2 mb-10 md:mb-0 md:pr-10">
                            <h2 className="text-3xl font-bold mb-6">輕鬆記錄每次訓練</h2>
                            <p className="text-gray-600 mb-6">我們的紀錄界面簡潔明了，讓你能夠專注於訓練本身，而不是複雜的操作。</p>
                            <ul className="space-y-4">
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-6 h-6 bg-black rounded-full flex items-center justify-center text-white mr-3">
                                        <span className="text-sm">1</span>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">選擇訓練動作</h4>
                                        <p className="text-gray-600 text-sm">從常用動作中選擇，或自定義新動作。</p>
                                    </div>
                                </li>
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-6 h-6 bg-black rounded-full flex items-center justify-center text-white mr-3">
                                        <span className="text-sm">2</span>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">記錄重量和次數</h4>
                                        <p className="text-gray-600 text-sm">簡單輸入每組的重量和重複次數。</p>
                                    </div>
                                </li>
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-6 h-6 bg-black rounded-full flex items-center justify-center text-white mr-3">
                                        <span className="text-sm">3</span>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">保存並查看進度</h4>
                                        <p className="text-gray-600 text-sm">一鍵保存，並立即查看與過去表現的比較。</p>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <div className="md:w-1/2">
                            <div className="bg-white rounded-lg shadow-xl p-6 md:p-8 mx-auto max-w-md">
                                <div className="flex justify-between items-center mb-6">
                                    <h3 className="text-lg font-medium">新增訓練記錄</h3>
                                    <span className="text-sm text-gray-500">今天</span>
                                </div>
                                <div className="mb-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">選擇動作</label>
                                    <div className="relative">
                                        <select className="block w-full p-3 border border-gray-300 rounded-md appearance-none focus:outline-none focus:ring-2 focus:ring-gray-200">
                                            <option>啞鈴臥推</option>
                                            <option>槓鈴深蹲</option>
                                            <option>引體向上</option>
                                            <option>硬舉</option>
                                            <option>+ 新增動作</option>
                                        </select>
                                        <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                                            <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                                <div className="mb-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">訓練部位</label>
                                    <div className="flex flex-wrap gap-2">
                                        <span className="px-3 py-1 bg-gray-100 rounded-full text-sm">胸部</span>
                                        <span className="px-3 py-1 bg-gray-100 rounded-full text-sm">三頭肌</span>
                                        <span className="px-3 py-1 bg-gray-100 rounded-full text-sm">肩膀</span>
                                        <span className="px-3 py-1 bg-white border border-dashed border-gray-300 rounded-full text-sm text-gray-500">+ 新增</span>
                                    </div>
                                </div>
                                <div className="mb-6">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="block text-sm font-medium text-gray-700">組數記錄</label>
                                        <button className="text-sm text-gray-500 hover:text-black">+ 新增組數</button>
                                    </div>
                                    <div className="space-y-3">
                                        <div className="flex items-center space-x-3">
                                            <span className="text-sm text-gray-500 w-16">組數 1</span>
                                            <div className="flex-1 flex space-x-2">
                                                <div className="flex-1">
                                                    <input type="number" placeholder="重量 (kg)" className="w-full p-2 border border-gray-300 rounded-md" />
                                                </div>
                                                <div className="flex-1">
                                                    <input type="number" placeholder="次數" className="w-full p-2 border border-gray-300 rounded-md" />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="flex items-center space-x-3">
                                            <span className="text-sm text-gray-500 w-16">組數 2</span>
                                            <div className="flex-1 flex space-x-2">
                                                <div className="flex-1">
                                                    <input type="number" placeholder="重量 (kg)" className="w-full p-2 border border-gray-300 rounded-md" />
                                                </div>
                                                <div className="flex-1">
                                                    <input type="number" placeholder="次數" className="w-full p-2 border border-gray-300 rounded-md" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="mb-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">備註</label>
                                    <textarea className="w-full p-3 border border-gray-300 rounded-md" rows="2" placeholder="記錄感受或細節..."></textarea>
                                </div>
                                <button className="w-full py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors">
                                    保存記錄
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* <!-- 統計分析區塊 --> */}
            <section id="stats" className="py-20 bg-white">
                <div className="container mx-auto px-4">
                    <div className="text-center mb-16">
                        <h2 className="text-3xl font-bold mb-4">數據驅動的訓練分析</h2>
                        <p className="text-gray-600 max-w-2xl mx-auto">透過視覺化圖表和詳細的數據分析，清晰了解你的進步和表現趨勢。</p>
                    </div>
                    
                    <div className="flex flex-col md:flex-row items-center">
                        <div className="md:w-1/2 mb-10 md:mb-0">
                            <div className="bg-white rounded-lg shadow-xl p-6 md:p-8 mx-auto max-w-md">
                                <div className="flex justify-between items-center mb-6">
                                    <h3 className="text-lg font-medium">啞鈴臥推進度</h3>
                                    <div className="flex items-center space-x-2">
                                        <button className="p-1 text-gray-500 hover:text-black">
                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
                                            </svg>
                                        </button>
                                        <span className="text-sm">最近 30 天</span>
                                        <button className="p-1 text-gray-500 hover:text-black">
                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
                                            </svg>
                                        </button>
                                    </div>
                                </div>
                                <div className="h-64 relative">
                                    <div className="absolute inset-0">
                                        {/* <!-- 圖表背景網格 --> */}
                                        <div className="h-full flex flex-col justify-between">
                                            <div className="border-b border-gray-100 relative">
                                                <span className="absolute -left-6 top-0 text-xs text-gray-400">30kg</span>
                                            </div>
                                            <div className="border-b border-gray-100 relative">
                                                <span className="absolute -left-6 top-0 text-xs text-gray-400">25kg</span>
                                            </div>
                                            <div className="border-b border-gray-100 relative">
                                                <span className="absolute -left-6 top-0 text-xs text-gray-400">20kg</span>
                                            </div>
                                            <div className="border-b border-gray-100 relative">
                                                <span className="absolute -left-6 top-0 text-xs text-gray-400">15kg</span>
                                            </div>
                                            <div className="border-b border-gray-100 relative">
                                                <span className="absolute -left-6 top-0 text-xs text-gray-400">10kg</span>
                                            </div>
                                        </div>
                                        
                                        {/* <!-- 圖表數據 --> */}
                                        <div className="absolute bottom-0 left-0 right-0 flex items-end justify-between h-full px-2">
                                            <div className="flex flex-col items-center">
                                                <div className="w-1 bg-black rounded-t-sm h-1/3 chart-animation"></div>
                                                <span className="text-xs mt-1 text-gray-500">10/1</span>
                                            </div>
                                            <div className="flex flex-col items-center">
                                                <div className="w-1 bg-black rounded-t-sm h-2/5 chart-animation"></div>
                                                <span className="text-xs mt-1 text-gray-500">10/8</span>
                                            </div>
                                            <div className="flex flex-col items-center">
                                                <div className="w-1 bg-black rounded-t-sm h-2/5 chart-animation"></div>
                                                <span className="text-xs mt-1 text-gray-500">10/15</span>
                                            </div>
                                            <div className="flex flex-col items-center">
                                                <div className="w-1 bg-black rounded-t-sm h-1/2 chart-animation"></div>
                                                <span className="text-xs mt-1 text-gray-500">10/22</span>
                                            </div>
                                            <div className="flex flex-col items-center">
                                                <div className="w-1 bg-black rounded-t-sm h-3/5 chart-animation"></div>
                                                <span className="text-xs mt-1 text-gray-500">今天</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="mt-6 pt-6 border-t border-gray-100">
                                    <div className="flex justify-between text-sm">
                                        <div>
                                            <div className="text-gray-500">最大重量</div>
                                            <div className="font-medium">25kg × 10</div>
                                        </div>
                                        <div>
                                            <div className="text-gray-500">進步</div>
                                            <div className="font-medium text-green-600">+5kg (25%)</div>
                                        </div>
                                        <div>
                                            <div className="text-gray-500">訓練頻率</div>
                                            <div className="font-medium">每週 2 次</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="md:w-1/2 md:pl-10">
                            <h3 className="text-2xl font-bold mb-4">了解你的訓練趨勢</h3>
                            <p className="text-gray-600 mb-6">我們的統計分析功能幫助你：</p>
                            <ul className="space-y-4">
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center mr-3">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">追蹤進步</h4>
                                        <p className="text-gray-600 text-sm">清晰看到每個動作的重量和次數變化。</p>
                                    </div>
                                </li>
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center mr-3">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">分析訓練量</h4>
                                        <p className="text-gray-600 text-sm">了解每週、每月的訓練頻率和總重量。</p>
                                    </div>
                                </li>
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center mr-3">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">身體平衡檢查</h4>
                                        <p className="text-gray-600 text-sm">確保你的訓練計劃均衡覆蓋所有肌群。</p>
                                    </div>
                                </li>
                                <li className="flex items-start">
                                    <div className="flex-shrink-0 w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center mr-3">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <h4 className="font-medium">休息時間優化</h4>
                                        <p className="text-gray-600 text-sm">根據你的表現數據，建議最佳的訓練頻率。</p>
                                    </div>
                                </li>
                            </ul>
                            <button className="mt-8 px-6 py-3 bg-black text-white rounded-md hover:bg-gray-800 transition-colors">
                                查看完整分析
                            </button>
                        </div>
                    </div>
                </div>
            </section>

            {/* <!-- 作者介紹區塊 --> */}
            <section id="author" className="py-20 bg-gray-50">
                <div className="container mx-auto px-4">
                    <div className="max-w-3xl mx-auto">
                        <div className="flex flex-col md:flex-row items-center md:items-start">
                            <div className="mb-6 md:mb-0 md:mr-8">
                                <div className="w-32 h-32 bg-gray-300 rounded-full overflow-hidden flex items-center justify-center">
                                    <svg className="w-16 h-16 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                                    </svg>
                                </div>
                            </div>
                            <div>
                                <h2 className="text-3xl font-bold mb-4">關於作者</h2>
                                <p className="text-gray-600 mb-4">
                                    嗨，我是 FitTrack 的創建者。作為一名健身愛好者和軟體開發者，我深知記錄和分析訓練數據的重要性。
                                </p>
                                <p className="text-gray-600 mb-6">
                                    我創建 FitTrack 的初衷是希望提供一個簡單而強大的工具，幫助每個健身愛好者更好地追蹤自己的進步，而不被複雜的操作所困擾。
                                </p>
                                <div className="flex space-x-4">
                                    <a href="#" className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition-colors">
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.342-3.369-1.342-.454-1.155-1.11-1.462-1.11-1.462-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.564 9.564 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.202 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.161 22 16.416 22 12c0-5.523-4.477-10-10-10z"></path>
                                        </svg>
                                    </a>
                                    <a href="#" className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition-colors">
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M22.162 5.656a8.384 8.384 0 01-2.402.658A4.196 4.196 0 0021.6 4c-.82.488-1.719.83-2.656 1.015a4.182 4.182 0 00-7.126 3.814 11.874 11.874 0 01-8.62-4.37 4.168 4.168 0 00-.566 2.103c0 1.45.738 2.731 1.86 3.481a4.168 4.168 0 01-1.894-.523v.052a4.185 4.185 0 003.355 4.101 4.21 4.21 0 01-1.89.072A4.185 4.185 0 007.97 16.65a8.394 8.394 0 01-6.191 1.732 11.83 11.83 0 006.41 1.88c7.693 0 11.9-6.373 11.9-11.9 0-.18-.005-.362-.013-.54a8.496 8.496 0 002.087-2.165z"></path>
                                        </svg>
                                    </a>
                                    <a href="#" className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition-colors">
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M16.98 0a6.9 6.9 0 01-1.36.08h-8.3A6.76 6.76 0 016 .08 6.27 6.27 0 004.56.66a5.23 5.23 0 00-1.88 1.82A5.4 5.4 0 002 4.66 6.48 6.48 0 001.96 7v10a7.05 7.05 0 00.08 1.32 5.7 5.7 0 00.54 1.62 5.41 5.41 0 001.92 1.9 5.7 5.7 0 002.16.66A7.05 7.05 0 008.02 24h8.3c.42 0 .83-.02 1.2-.08a5.97 5.97 0 002.14-.66 5.23 5.23 0 001.88-1.82 5.4 5.4 0 00.68-2.18 6.48 6.48 0 00.08-1.3V7.04c0-.42-.02-.83-.08-1.2a5.97 5.97 0 00-.66-2.18 5.4 5.4 0 00-1.82-1.88 5.7 5.7 0 00-2.18-.68A6.48 6.48 0 0016.98 0zm-3.4 7.02c.66 0 1.14.5 1.14 1.14a1.14 1.14 0 01-2.28 0c0-.64.5-1.14 1.14-1.14zm-4.04 2.5c1.5 0 2.7 1.2 2.7 2.7s-1.2 2.7-2.7 2.7-2.7-1.2-2.7-2.7 1.2-2.7 2.7-2.7zm0-1.06a3.8 3.8 0 00-3.8 3.8 3.8 3.8 0 003.8 3.8 3.8 3.8 0 003.8-3.8 3.8 3.8 0 00-3.8-3.8zm7.86-2.76c.88 0 1.6.72 1.6 1.6 0 .88-.72 1.6-1.6 1.6-.88 0-1.6-.72-1.6-1.6 0-.88.72-1.6 1.6-1.6z"></path>
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* <!-- 登入註冊區塊 --> */}
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

            {/* <!-- 頁尾 --> */}
            <footer className="bg-gray-100 py-10">
                <div className="container mx-auto px-4">
                    <div className="flex flex-col md:flex-row justify-between items-center">
                        <div className="mb-6 md:mb-0">
                            <div className="flex items-center space-x-2">
                                <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M20.24 12.24a6 6 0 0 0-8.49-8.49L9 6l1.5 1.5L12 6l-1.5-1.5a3 3 0 1 1 4.24 4.24L12 12l-1.5-1.5L9 12l2.25 2.25a6 6 0 0 0 8.49-8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M3.76 12.24a6 6 0 0 0 8.49 8.49L15 18l-1.5-1.5L12 18l1.5 1.5a3 3 0 1 1-4.24-4.24L12 12l1.5 1.5L15 12l-2.25-2.25a6 6 0 0 0-8.49 8.49z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                <span className="font-bold">FitTrack</span>
                            </div>
                            <p className="text-sm text-gray-500 mt-2">© 2023 FitTrack. 保留所有權利。</p>
                        </div>
                        <div className="flex space-x-8">
                            <a href="#" className="text-sm text-gray-600 hover:text-black">隱私政策</a>
                            <a href="#" className="text-sm text-gray-600 hover:text-black">服務條款</a>
                            <a href="#" className="text-sm text-gray-600 hover:text-black">聯絡我們</a>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    );
}

export default LandingPage;
