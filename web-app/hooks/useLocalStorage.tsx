import { Dispatch, SetStateAction, useEffect, useState } from 'react'

function useLocalStorage<T>(
  _key: string,
  _initialValue: T | (() => T),
): [T, Dispatch<SetStateAction<T>>]
function useLocalStorage<T = undefined>(
  _key: string,
): [T | undefined, Dispatch<SetStateAction<T | undefined>>]

function useLocalStorage<T = undefined>(
  key: string,
  initialValue?: T | (() => T),
) {
  const [value, setValue] = useState(() => {
    if (typeof window !== 'undefined') {
      const storedValue = localStorage.getItem(key)
      return storedValue ? (JSON.parse(storedValue) as T) : initialValue
    } else {
      return initialValue
    }
  })

  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(value))
  }, [key, value])

  useEffect(() => {
    const handleStorageChange = (event: StorageEvent) => {
      if (event.key === key)
        setValue(event.newValue ? JSON.parse(event.newValue) : initialValue)
    }

    window.addEventListener('storage', handleStorageChange)

    return () => {
      window.removeEventListener('storage', handleStorageChange)
    }
  }, [key, initialValue])

  return [value, setValue] as const
}

export default useLocalStorage
