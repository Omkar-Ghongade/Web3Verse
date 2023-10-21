import { useContext } from 'react'
import { AuthContext } from '../authProvider'

const useAuthContext = () => {
  const _authContext = useContext(AuthContext)

  if (!_authContext) {
    throw new Error(
      'useAuthContext has to be used within <AuthContext.Provider>',
    )
  }

  return _authContext
}

export default useAuthContext
