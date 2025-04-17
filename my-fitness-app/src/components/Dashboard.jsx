import React from "react";
import MuscleGroupChart from "./MuscleGroupChart";
import WeeklySummaryChart from "./WeeklySummaryChart";
import PopularTypesChart from "./PopularTypesChart";
import ProgressChart from "./ProgressChart";
import WeeklyFrequencyChart from "./WeeklyFrequencyChart";

function Dashboard() {
  return (
    <div className="space-y-8 p-4 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-4">📊 健身分析總覽</h1>
      <MuscleGroupChart />
      <WeeklySummaryChart />
      <PopularTypesChart />
      <ProgressChart />
      <WeeklyFrequencyChart />
    </div>
  );
}

export default Dashboard;
