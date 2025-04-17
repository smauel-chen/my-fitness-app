// src/components/WorkoutTypeManager.jsx
import AddWorkoutTypeForm from "./AddWorkoutTypeForm";
import WorkoutTypeList from "./WorkoutTypeList";

function WorkoutTypeManager() {
  return (
    <div className="p-4 max-w-2xl mx-auto space-y-6">
      <AddWorkoutTypeForm />
      <WorkoutTypeList />
    </div>
  );
}

export default WorkoutTypeManager;
