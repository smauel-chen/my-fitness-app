import React from "react";

function FeatureCard({ title, description, buttonText, onClick }) {
  return (
    <div className="bg-white rounded-lg shadow-sm p-6 border flex flex-col justify-between">
      <div>
        <h3 className="text-lg font-semibold text-gray-800 mb-2">{title}</h3>
        <p className="text-sm text-gray-600 mb-4">{description}</p>
      </div>
      <button
        onClick={onClick}
        className="mt-auto bg-neutral-800 text-white px-4 py-2 rounded hover:bg-neutral-700 transition"
      >
        {buttonText}
      </button>
    </div>
  );
}

export default FeatureCard;
