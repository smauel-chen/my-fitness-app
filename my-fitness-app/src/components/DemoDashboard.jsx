import React from "react";
import FeatureCard from "./FeatureCard";

import { useNavigate } from "react-router-dom";

function DemoDashboard() {

    const navigate = useNavigate();

    const features = [
      {
        title: "📝 訓練紀錄",
        description: "快速新增訓練組合與註解，方便回顧每次的努力與細節。",
        buttonText: "前往紀錄",
        onClick: () => navigate("/"),
      },
      {
        title: "📈 分析總覽",
        description: "查看每週訓練次數、重量總和與各肌群的訓練量統計，幫助你追蹤進步！",
        buttonText: "查看儀表板",
        onClick: () => navigate("/dashboard"),
      },
      {
        title: "🧩 動作管理",
        description: "建立屬於你的訓練動作類型，自訂肌群與名稱，個人化每個動作。",
        buttonText: "管理動作",
        onClick: () => navigate("/types"),
      }
    ];

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
        <h1 className="text-3xl font-bold text-center "> 健身紀錄系統 Demo</h1>
        {/* 系統特色介紹 */}
        <div className="bg-white rounded-lg shadow-sm p-6 space-y-4 mt-12 border border-gray-200">
        <h2 className="text-2xl font-bold">🔍 系統特色 Highlights</h2>
        <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>訓練資料可視化，幫助追蹤每週進度與成長趨勢</li>
            <li>採用分層架構（Controller / DTO / Exception / Repository）強化維護性</li>
            <li>支援 JWT 驗證機制，確保資料安全與登入狀態</li>
            <li>前後端分離：React 串接 Spring Boot API，畫面互動流暢</li>
            <li>Swagger 文件與全站 DTO 支援，貼近實務後端開發流程</li>
        </ul>
        </div>
        {/* 操作流程與功能說明 */}
        <div className="bg-white rounded-lg shadow-sm p-6 space-y-4 mt-12 border border-gray-200">
        <h2 className="text-2xl font-bold">操作流程與主要功能</h2>
        <ol className="list-decimal list-inside space-y-2 text-gray-700">
            <li>
            <b>使用者登入：</b> 使用預先建立的帳號進行登入並獲取 JWT Token。
            </li>
            <li>
            <b>新增訓練紀錄：</b> 輸入訓練動作、重量、次數，建立每日的訓練 Session。
            </li>
            <li>
            <b>圖表分析：</b> 系統自動統計每週訓練次數、總重量、熱門訓練類型，並以圖表方式顯示。
            </li>
            <li>
            <b>訓練動作管理：</b> 新增 / 刪除訓練動作與對應肌群，提供自訂化的訓練配置。
            </li>
            <li>
            <b>資料安全性：</b> 所有資料需登入後才可操作，並以 JWT 驗證機制確保資料安全。
            </li>
        </ol>
        </div>

        <section className="space-y-2">
            <h2 className="text-xl font-semibold text-gray-800">專案介紹</h2>
            <p className="text-gray-600">
            本系統是一套科學化健身紀錄平台，使用者可以記錄每日訓練內容、分析肌群使用狀況、查看個人表現趨勢。
            </p>

            <div className="grid md:grid-cols-3 gap-6 mt-8">
            {features.map((feature, idx) => (
            <FeatureCard key={idx} {...feature} />
            ))}
            </div>    
        </section>



        <section className="space-y-2">
            <h2 className="text-xl font-semibold text-gray-800">使用技術</h2>
            <ul className="list-disc list-inside text-gray-600 space-y-1">
            <li><b>後端：</b>Java + Spring Boot + JPA + JWT + 自訂 Exception 架構</li>
            <li><b>資料庫：</b>MySQL（透過 Docker 管理）</li>
            <li><b>前端：</b>React + Axios + Tailwind CSS</li>
            <li><b>API 文件：</b>Swagger UI</li>
            </ul>
        </section>

        <section className="space-y-2">
            <h2 className="text-xl font-semibold text-gray-800">我的角色</h2>
            <p className="text-gray-600">
            本專案為個人獨立開發，涵蓋需求分析、前後端設計與實作、測試與錯誤處理設計等。
            </p>
        </section>


    </div>
  );
}

export default DemoDashboard;
