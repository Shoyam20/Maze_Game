export default function StatCard({ label, value, accent }) {
  return (
    <div className="stat-card">
      <span className="stat-label">{label}</span>
      <strong className={`stat-value ${accent || ''}`}>{value}</strong>
    </div>
  );
}
