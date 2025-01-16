import { ReactNode } from 'react';

export const ProtectedRoute = ({
  children,
}: {
  children: ReactNode;
}): JSX.Element => {
  return <>{children}</>;
};
