const controls = [
  ['UP', '↑'],
  ['LEFT', '←'],
  ['DOWN', '↓'],
  ['RIGHT', '→']
];

export default function ControlPad({ onMove }) {
  return (
    <div className="control-pad">
      <button onClick={() => onMove('UP')}>W</button>
      <div className="control-row">
        <button onClick={() => onMove('LEFT')}>A</button>
        <button onClick={() => onMove('DOWN')}>S</button>
        <button onClick={() => onMove('RIGHT')}>D</button>
      </div>
      <div className="hint-row">Keyboard: WASD</div>
    </div>
  );
}
