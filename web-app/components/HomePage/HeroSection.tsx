import {
  Button,
  Container,
  Group,
  Image,
  List,
  rem,
  Text,
  ThemeIcon,
  Title,
  useMantineTheme,
} from '@mantine/core'
import { IconCheck } from '@tabler/icons-react'
import { ethos, SignInButton } from 'ethos-connect'
import { useRouter } from 'next/router'
import { useEffect } from 'react'
import classes from './HeroSection.module.css'

export function HeroSection() {
  const { status, wallet } = ethos.useWallet()
  const router = useRouter()

  useEffect(() => {
    if (wallet?.address) {
      router.push('/app/dashboard')
    }
  }, [status, wallet])

  const theme = useMantineTheme()

  return (
    <Container size="md">
      <div className={classes.inner}>
        <div className={classes.content}>
          <Title className={classes.title}>
            Your <span className={classes.highlight}>World</span>, Your <br />
            Way, In VR Today!
          </Title>
          <Text c="dimmed" mt="md">
            MetaVerse is a fully decentralized, open source, virtual world
            platform. MetaVerse is built on the Avalanche, SUI blockchain and is
            accessible via web3 enabled browsers. MetaVerse is a place where
            creators can build and monetize their own virtual worlds.
          </Text>
          <List
            mt={30}
            spacing="sm"
            size="sm"
            icon={
              <ThemeIcon size={20} radius="xl" color="pink">
                <IconCheck
                  style={{ width: rem(12), height: rem(12) }}
                  stroke={1.5}
                />
              </ThemeIcon>
            }
          >
            <List.Item>
              Fully decentralized, open source, virtual world platform built on
              the SUI blockchain and Avalanche.
            </List.Item>
            <List.Item>
              Secure, scalable, and energy efficient blockchain that powers
            </List.Item>
            <List.Item>
              the next generation of decentralized applications. MetaVerse is
              accessible via Facebook Oculus
            </List.Item>
          </List>

          <Group mt={30}>
            <SignInButton
              style={{
                backgroundColor: theme.colors.pink[6],
                color: theme.colors.pink[0],
                padding: '0.70rem 1.5rem',
                borderRadius: '1rem',
              }}
            >
              {/* <Button radius="xl" fw={500} size="md"> */}
              Connect Wallet
              {/* </Button> */}
            </SignInButton>
            <Button
              variant="default"
              radius="xl"
              size="md"
              fw={500}
              className={classes.control}
            >
              Learn More
            </Button>
          </Group>
        </div>
        <Image src={'./brand-image.jpeg'} className={classes.image} />
      </div>
    </Container>
  )
}
