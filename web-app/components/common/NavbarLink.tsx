import {
  Box,
  Button,
  Flex,
  Group,
  Text,
  ThemeIcon,
  UnstyledButton,
  useMantineTheme,
} from '@mantine/core'
import {
  IconArrowsShuffle,
  IconHome,
  IconListDetails,
  IconPhoto,
  IconUser,
} from '@tabler/icons-react'
import { ethos } from 'ethos-connect'
import { useRouter } from 'next/router'
import { useCallback } from 'react'

const adminNavLinks = [
  {
    label: 'Dashboard',
    icon: <IconHome size={'1.1rem'} stroke={1.75} />,
    href: '/app/dashboard',
  },
  {
    label: 'Mint NFT',
    icon: <IconPhoto size={'1.1rem'} stroke={1.75} />,
    href: '/app/mint-nft',
  },
  {
    label: 'NFT Collection',
    icon: <IconListDetails size={'1.1rem'} stroke={1.75} />,
    href: '/app/my-nfts',
  },
  {
    label: 'Exchange SUI',
    icon: <IconArrowsShuffle size={'1.1rem'} stroke={1.75} />,
    href: '/app/cross-chain',
  },
  {
    label: 'Your Avatar',
    icon: <IconUser size={'1.1rem'} stroke={1.75} />,
    href: '/app/avatars',
  },
]

export const NavbarLinks = () => {
  const router = useRouter()
  const theme = useMantineTheme()

  const isActive = (href: string) => {
    return router.pathname === href
  }

  const { wallet } = ethos.useWallet()

  const disconnect = useCallback(() => {
    if (!wallet) return
    wallet.disconnect()
  }, [wallet])

  return (
    <Box
      style={{
        position: 'relative',
        height: '100%',
      }}
    >
      <Flex
        style={{
          flex: 1,
        }}
        direction={'column'}
        justify={'start'}
        align={'center'}
        gap={10}
      >
        {adminNavLinks.map((link) => (
          <UnstyledButton
            onClick={() => router.push(link.href)}
            key={link.label}
            style={{
              backgroundColor: isActive(link.href)
                ? theme.colors.pink[8]
                : 'transparent',
              borderRadius: 4,
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'flex-start',
              gap: 10,
            }}
          >
            <Flex w={'100%'} p={'xs'}>
              <Group>
                <ThemeIcon variant="light" color="pink" size="md" radius="sm">
                  {link.icon}
                </ThemeIcon>
                <Text size="sm" fw={500} c={'white'}>
                  {link.label}
                </Text>
              </Group>
            </Flex>
          </UnstyledButton>
        ))}
      </Flex>
      <Box style={{ position: 'absolute', bottom: 0, left: 0, right: 0 }}>
        <Button
          color="pink"
          fw={500}
          variant="outline"
          fullWidth
          onClick={disconnect}
        >
          Disconnect
        </Button>
      </Box>
    </Box>
  )
}
