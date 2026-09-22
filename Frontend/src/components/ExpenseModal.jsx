import { useState, useEffect } from "react";
import API from "../services/api";

export default function ExpenseModal({ isOpen, onClose, onRefresh, editData = null }) {
  const [formData, setFormData] = useState({
    title: "",
    amount: "",
    category: "Food",
    date: new Date().toISOString().split("T")[0]
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const getFlaggedExpenses =async()=>{
    try{
      axios.get('api/expense/review')
      console.log("flagged expenses:", response.data)
      return response.data;
    }catch(error){
      console.log("Error fetching review",error);
      return[];
    }
  };


  useEffect(() => {
    if (editData) {
      setFormData({
        title: editData.title,
        amount: editData.amount,
        category: editData.category,
        date: editData.date
      });
    } else {
      setFormData({ 
        title: "", 
        amount: "", 
        category: "Food", 
        date: new Date().toISOString().split("T")[0] 
      });
    }
    setError("");
  }, [editData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    
    try {
      if (editData) {
        await API.put(`/api/expenses/${editData.id}`, formData);
      } else {
        await API.post("/api/expenses/add", formData);
      }
      
      // We must await the refresh before closing to ensure Dashboard has new data
      await onRefresh(); 
      onClose();
    } catch (err) {
      // Capture the specific error message from the backend
      const serverMsg = err.response?.data?.message || err.response?.data;
      setError(serverMsg || "Failed to process expense. Check your balance.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl border border-gray-100">
        <h2 className="text-2xl font-black mb-6 text-gray-800">
          {editData ? "Edit Expense" : "New Expense Log"}
        </h2>
        
        {error && (
          <div className="bg-red-50 text-red-600 p-4 rounded-2xl mb-6 text-sm font-bold border border-red-100 flex items-center gap-2">
            <span>⚠️</span> {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1">
            <label className="text-[10px] font-black text-gray-400 uppercase ml-1">Label</label>
            <input
              type="text"
              className="w-full p-4 bg-gray-50 rounded-2xl outline-none border focus:border-blue-500 font-medium"
              value={formData.title}
              placeholder="e.g. Starbucks"
              onChange={(e) => setFormData({...formData, title: e.target.value})}
              required
            />
          </div>

          <div className="flex gap-4">
            <div className="w-1/2 space-y-1">
              <label className="text-[10px] font-black text-gray-400 uppercase ml-1">Amount (₹)</label>
              <input
                type="number"
                className="w-full p-4 bg-gray-50 rounded-2xl outline-none border focus:border-blue-500 font-bold"
                value={formData.amount}
                placeholder="0.00"
                onChange={(e) => setFormData({...formData, amount: e.target.value})}
                required
              />
            </div>
            <div className="w-1/2 space-y-1">
              <label className="text-[10px] font-black text-gray-400 uppercase ml-1">Category</label>
              <select 
                className="w-full p-4 bg-gray-50 rounded-2xl outline-none border font-bold text-gray-600"
                value={formData.category}
                onChange={(e) => setFormData({...formData, category: e.target.value})}
              >
                <option value="Food">Food</option>
                <option value="Transport">Transport</option>
                <option value="Shopping">Shopping</option>
                <option value="Bills">Bills</option>
              </select>
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-[10px] font-black text-gray-400 uppercase ml-1">Transaction Date</label>
            <input
              type="date"
              className="w-full p-4 bg-gray-50 rounded-2xl outline-none border font-medium"
              value={formData.date}
              onChange={(e) => setFormData({...formData, date: e.target.value})}
            />
          </div>

          <div className="flex gap-4 pt-6">
            <button type="button" onClick={onClose} className="flex-1 font-black text-gray-400 hover:text-gray-600 transition">Cancel</button>
            <button 
              type="submit" 
              disabled={loading}
              className="flex-1 py-4 bg-blue-600 text-white rounded-2xl font-black shadow-lg shadow-blue-100 hover:bg-blue-700 transition disabled:opacity-50"
            >
              {loading ? "Saving..." : editData ? "Update Log" : "Confirm Log"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}