import { useState } from 'react';
import { Handle, Position } from 'reactflow';

const CustomClassNode = ({ data }) => {
  const [isHovered, setIsHovered] = useState(false);
  
  return (
    <div 
      className={`relative group transition-all duration-500 transform ${isHovered ? 'scale-105' : 'scale-100'}`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Glowing background effect */}
      <div className="absolute -inset-2 bg-gradient-to-r from-purple-500 via-pink-500 to-cyan-500 rounded-3xl opacity-20 blur-xl group-hover:opacity-40 transition-all duration-500 animate-pulse"></div>
      
      {/* Main node container */}      <div className="relative bg-slate-900/90 backdrop-blur-xl border border-purple-500/30 rounded-2xl shadow-2xl min-w-[280px] overflow-hidden">
        
        {/* Header with class name */}
        <div className="relative bg-slate-800/80 backdrop-blur-sm p-4 border-b border-purple-400/30">
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 rounded-full bg-gradient-to-r from-green-400 to-emerald-500 animate-pulse"></div>
            <h3 className="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-300 to-pink-300 tracking-wide">
              {data.label}
            </h3>
          </div>
        </div>
        
        {/* Content section */}
        <div className="relative p-4 space-y-4">
          {/* Attributes section */}
          <div className="space-y-2">
            <div className="flex items-center space-x-2">
              <div className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></div>
              <h4 className="text-sm font-semibold text-cyan-300 uppercase tracking-wider">Attributes</h4>
            </div>
            <div className="space-y-1 pl-4">
              {data.attributes?.map((attr, idx) => (
                <div 
                  key={idx} 
                  className="text-sm text-slate-300 hover:text-cyan-200 transition-colors duration-300 flex items-center space-x-2 p-1 rounded-lg hover:bg-cyan-500/10"
                >                  <span className="w-1 h-1 rounded-full bg-cyan-400/60"></span>                  <span className="font-mono">
                    {attr}
                  </span>
                </div>
              ))}
            </div>
          </div>
          
          {/* Methods section */}
          {data.methods && data.methods.length > 0 && (
            <>
              <div className="w-full h-px bg-gradient-to-r from-transparent via-purple-400/50 to-transparent"></div>
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  <div className="w-2 h-2 rounded-full bg-pink-400 animate-pulse"></div>
                  <h4 className="text-sm font-semibold text-pink-300 uppercase tracking-wider">Methods</h4>
                </div>
                <div className="space-y-1 pl-4">                {data.methods.map((method, idx) => (
                    <div 
                      key={idx} 
                      className="text-sm text-slate-300 hover:text-pink-200 transition-colors duration-300 flex items-center space-x-2 p-1 rounded-lg hover:bg-pink-500/10"
                    >
                      <span className="w-1 h-1 rounded-full bg-pink-400/60"></span>
                      <span className="font-mono">
                        {method.name}(
                        {method.parameters?.join(", ") || ""}
                        ): {method.returnType}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </>
          )}
        </div>

        {/* Floating particles effect */}
        <div className="absolute inset-0 pointer-events-none overflow-hidden rounded-2xl">
          <div className="absolute top-2 left-2 w-1 h-1 bg-cyan-400 rounded-full animate-ping opacity-40"></div>
          <div className="absolute top-4 right-4 w-1 h-1 bg-pink-400 rounded-full animate-ping opacity-40 animation-delay-1000"></div>
          <div className="absolute bottom-4 left-6 w-1 h-1 bg-purple-400 rounded-full animate-ping opacity-40 animation-delay-2000"></div>
        </div>
      </div>
      
      {/* Connection handles */}
      <Handle 
        type="target" 
        position={Position.Top} 
        className="w-3 h-3 bg-gradient-to-r from-cyan-400 to-purple-500 border-2 border-slate-800 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
      <Handle 
        type="source" 
        position={Position.Bottom} 
        className="w-3 h-3 bg-gradient-to-r from-pink-400 to-cyan-500 border-2 border-slate-800 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
      <Handle 
        type="source" 
        position={Position.Left} 
        className="w-3 h-3 bg-gradient-to-r from-purple-400 to-pink-500 border-2 border-slate-800 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
      <Handle 
        type="source" 
        position={Position.Right} 
        className="w-3 h-3 bg-gradient-to-r from-cyan-400 to-purple-500 border-2 border-slate-800 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
    </div>
  );
};

export default CustomClassNode;
