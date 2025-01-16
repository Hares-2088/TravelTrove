import { ReactNode } from 'react';

interface ProtectedRoute {
  children: ReactNode;
}

export const ProtectedRoute = ({ children }: ProtectedRoute): JSX.Element => {
  return <>{children}</>;
};
