import { useEffect, useMemo, useState } from 'react';
import ReactFlow, { Background, Controls } from 'reactflow';
import 'reactflow/dist/style.css';
import CustomClassNode from './CustomClassNode';

const nodeTypes = {
  default: CustomClassNode,
};

const ClassDiagram = ({ diagramData }) => {
  const [selectedNode, setSelectedNode] = useState(null);

  useEffect(() => {
    console.log('Received diagram data:', diagramData);
  }, [diagramData]);

  const elements = useMemo(() => {
    if (!diagramData?.entities) {
      console.warn('No entities found in diagram data');
      return [];
    }

    const nodes = diagramData.entities.map((entity, index) => {
      const angle = (index * 2 * Math.PI) / diagramData.entities.length;
      const radius = Math.max(300, diagramData.entities.length * 80);
      const x = radius * Math.cos(angle);
      const y = radius * Math.sin(angle);

      return {
        id: entity.name,
        type: 'default',
        position: { x, y },
        data: {
          label: entity.name,
          attributes: entity.attributes || [],
          methods: entity.methods || []
        },
      };
    });

    const edges = diagramData.relationships?.map((rel, idx) => ({
      id: `e${rel.subject}-${rel.object}-${idx}`,
      source: rel.subject,
      target: rel.object,
      label: rel.verb,
      animated: true,
      style: {
        stroke: '#a855f7',
        strokeWidth: 2,
      },
      labelStyle: {
        fill: '#e2e8f0',
        fontWeight: 500,
        backgroundColor: 'rgba(15, 23, 42, 0.75)',
        padding: '4px 8px',
        borderRadius: '4px',
      },
      labelBgStyle: {
        fill: 'none',
      },
      markerEnd: {
        type: 'arrowclosed',
        color: '#a855f7',
      },
    })) || [];

    return [...nodes, ...edges];
  }, [diagramData]);

  return (
    <div className="w-full h-[600px] bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 relative overflow-hidden rounded-xl">
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl animate-pulse animation-delay-2000"></div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-pink-500/5 rounded-full blur-3xl animate-pulse animation-delay-4000"></div>
      </div>

      <div className="relative z-10 w-full h-full">
        <ReactFlow
          nodes={elements.filter(el => !el.source)}
          edges={elements.filter(el => el.source)}
          nodeTypes={nodeTypes}
          onNodeClick={(_, node) => setSelectedNode(node.id === selectedNode ? null : node.id)}
          fitView
          fitViewOptions={{ padding: 50 }}
          defaultViewport={{ x: 0, y: 0, zoom: 0.8 }}
          minZoom={0.2}
          maxZoom={2}
          className="react-flow-dark-theme"
        >
          <Background
            color="#374151"
            variant="dots"
            gap={30}
            size={2}
            className="opacity-30"
          />
          
          <Controls
            className="react-flow-controls-custom"
            style={{
              background: 'rgba(15, 23, 42, 0.8)',
              border: '1px solid rgba(168, 85, 247, 0.3)',
              borderRadius: '12px',
              backdropFilter: 'blur(12px)',
            }}
          />
        </ReactFlow>
      </div>

      <style jsx>{`
        .animation-delay-1000 { animation-delay: 1s; }
        .animation-delay-2000 { animation-delay: 2s; }
        .animation-delay-4000 { animation-delay: 4s; }
        
        :global(.react-flow-dark-theme .react-flow__node) {
          background: transparent;
          border: none;
        }
        
        :global(.react-flow-controls-custom button) {
          background: rgba(15, 23, 42, 0.9) !important;
          border: 1px solid rgba(168, 85, 247, 0.3) !important;
          color: #e2e8f0 !important;
          transition: all 0.3s ease !important;
        }
        
        :global(.react-flow-controls-custom button:hover) {
          background: rgba(168, 85, 247, 0.2) !important;
          border-color: rgba(168, 85, 247, 0.6) !important;
          transform: scale(1.05);
        }
        
        :global(.react-flow__edge-path) {
          filter: drop-shadow(0 0 8px rgba(168, 85, 247, 0.3));
        }
        
        :global(.react-flow__edge.animated .react-flow__edge-path) {
          stroke-dasharray: 20;
          animation: dashdraw 2s linear infinite;
        }

        :global(.react-flow__edge-text) {
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
        }
        
        @keyframes dashdraw {
          to {
            stroke-dashoffset: -40;
          }
        }
      `}</style>
    </div>
  );
};

export default ClassDiagram;
