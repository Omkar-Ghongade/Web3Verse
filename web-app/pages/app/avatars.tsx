import { Avatar } from '@readyplayerme/visage'
import { useEffect, useState } from 'react'
import { MainLayout } from '../../layout/MainLayout'

export default function AvatarPage() {
  const [avatar, setAvatar] = useState('')

  useEffect(() => {
    setAvatar('https://models.readyplayer.me/653296776b758d149a6db66c.glb')
  }, [])

  return (
    <MainLayout>
      <div>
        {avatar ? (
          <Avatar
            modelSrc={
              'https://models.readyplayer.me/653296776b758d149a6db66c.glb'
            }
            style={{
              width: '100%',
              height: '100%',
              minHeight: '100vh',
              minWidth: '100vw',
              position: 'absolute',
              top: '0',
              left: '0',
            }}
          />
        ) : (
          <div>Loading...</div>
        )}
      </div>
    </MainLayout>
  )
}
