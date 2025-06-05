import React, { createContext, useContext, useEffect, useState } from 'react';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

interface AuthContextType {
  keycloak: KeycloakInstance | null;
  initialized: boolean;
  isAuthenticated: boolean;
  login: () => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'myrealm',
  clientId: 'spring-client'
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [keycloak, setKeycloak] = useState<KeycloakInstance | null>(null);
  const [initialized, setInitialized] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const initKeycloak = async () => {
      const keycloakInstance = new (Keycloak as any)({
        url: keycloakConfig.url,
        realm: keycloakConfig.realm,
        clientId: keycloakConfig.clientId
      });

      try {
        const authenticated = await keycloakInstance.init({
          onLoad: 'login-required',
          checkLoginIframe: false
        });

        setKeycloak(keycloakInstance);
        setIsAuthenticated(authenticated);
      } catch (error) {
        console.error('Keycloak initialization error:', error);
      } finally {
        setInitialized(true);
      }
    };

    initKeycloak();
  }, []);

  const login = async () => {
    if (keycloak) {
      await keycloak.login();
    }
  };

  const logout = async () => {
    if (keycloak) {
      await keycloak.logout({ redirectUri: window.location.origin });
    }
  };

  return (
    <AuthContext.Provider value={{ keycloak, initialized, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};