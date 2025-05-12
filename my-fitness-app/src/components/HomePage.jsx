import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  const sections = [
    { id: "intro", label: "基本介紹" },
    { id: "records", label: "紀錄介紹" },
    { id: "charts", label: "圖表介紹" },
    { id: "types", label: "動作資料庫" },
    { id: "about", label: "設計者" },
  ];

  return (
    <div className="relative">
      {/* Fixed Navbar */}
      <nav className="bg-gray-100 fixed top-0 left-0 w-full shadow z-50 flex justify-between items-center px-6 py-4">
        <div className="font-bold text-lg text-stone-800">健身紀錄系統</div>
        <div className="flex space-x-4">
          {sections.map((sec) => (
            <a
              key={sec.id}
              href={`#${sec.id}`}
              className="text-gray-600 hover:text-blue-600 text-sm"
            >
              {sec.label}
            </a>
          ))}
          <button
            onClick={() => navigate("/login")}
            className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700 text-sm"
          >
            登入
          </button>
        </div>
      </nav>

      {/* Page Content */}
      <div className="pt-20 space-y-20">
        {/* Intro */}
        <section id="intro" className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-2xl font-bold mb-4">歡迎使用健身紀錄系統</h2>
          <p className="text-gray-600">
            我們提供簡單、流暢的健身紀錄體驗，幫助你追蹤每一次進步。
          </p>
        </section>

        {/* Records */}
        <section id="records" className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-2xl font-bold mb-4">紀錄介紹</h2>
          <p className="text-gray-600">
            輕鬆新增、編輯、刪除每一次訓練，完整記錄每一組數據。
          </p>
        </section>

        {/* Charts */}
        <section id="charts" className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-2xl font-bold mb-4">圖表介紹</h2>
          <p className="text-gray-600">
            視覺化分析你的訓練數據，了解熱門動作、進步曲線與每週表現。
          </p>
        </section>

        {/* Types */}
        <section id="types" className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-2xl font-bold mb-4">動作資料庫</h2>
          <p className="text-gray-600">
            自定義屬於你的訓練動作，建立個人專屬的動作清單。
          </p>
        </section>

        {/* About */}
        <section id="about" className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-2xl font-bold mb-4">關於設計者</h2>
          <p className="text-gray-600">
            我是 [你的名字]，熱愛健身與程式的開發者，希望用技術幫助大家更科學地訓練。
          </p>
        </section>
      </div>
    </div>
  );
}

export default HomePage;
