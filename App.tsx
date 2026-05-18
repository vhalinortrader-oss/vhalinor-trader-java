/**
 * VHALINOR TRADER - Aplicação Principal
 * =======================================
 * Aplicação React principal que integra todos os componentes
 * do VHALINOR TRADER com base nos arquivos da pasta src.
 *
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import VhalinorInterface from './components/VhalinorInterface';
import VhalinorDashboard from './components/VhalinorDashboard';

export default function App() {
  const [currentView, setCurrentView] = useState<'interface' | 'dashboard'>('interface');
  const [darkMode, setDarkMode] = useState(true);

  useEffect(() => {
    // Aplicar tema ao documento
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [darkMode]);

  return (
    <div className="App">
      {currentView === 'interface' ? (
        <VhalinorInterface />
      ) : (
        <VhalinorDashboard />
      )}
      
      {/* Toggle de navegação */}
      <div className="fixed bottom-4 right-4 flex flex-col space-y-2">
        <button
          onClick={() => setCurrentView(currentView === 'interface' ? 'dashboard' : 'interface')}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow-lg transition-colors"
        >
          {currentView === 'interface' ? 'Dashboard' : 'Interface'}
        </button>
        
        <button
          onClick={() => setDarkMode(!darkMode)}
          className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg shadow-lg transition-colors"
        >
          {darkMode ? '☀️' : '🌙'}
        </button>
      </div>
    </div>
  );
}
