import { Card, Flex, Stack, Text, useMantineTheme } from '@mantine/core'
import { ethos } from 'ethos-connect'
import { useRouter } from 'next/router'
import LineChart from '../../components/LineChat'
import { MainLayout } from '../../layout/MainLayout'

export default function DashboardPage() {
  const { status, wallet } = ethos.useWallet()
  const router = useRouter()
  const theme = useMantineTheme()

  const generateRandomData = () => {
    const data = []
    const startDate = new Date('2021-10-19').getTime() // Start date
    const oneDay = 24 * 60 * 60 * 1000 // One day in milliseconds

    for (let i = 0; i < 100; i++) {
      const date = new Date(startDate + i * oneDay).getTime() // Incrementing date
      const value = 10000 + Math.floor(Math.random() * 10000) // Random value between 10,000 and 19,999
      data.push([date, value])
    }

    return data
  }

  const data = generateRandomData()

  return (
    <MainLayout>
      <Stack>
        <Flex gap={10}>
          <Card radius="md" padding="xl" bg={theme.colors.dark[8]} w={'40%'}>
            <Text size="md" tt="uppercase" fw={500} c="pink" ff={'monospace'}>
              Total Balance (SUI)
            </Text>
            <Text fz="lg" fw={500} c={'white'} ff={'monospace'}>
              {wallet?.contents?.suiBalance.toNumber()! / 1e9} SUI
            </Text>
          </Card>

          <Card radius="md" padding="xl" bg={theme.colors.dark[8]} w={'100%'}>
            <Text size="md" tt="uppercase" fw={500} c="pink" ff={'monospace'}>
              Your Account Address
            </Text>
            <Text fz="lg" fw={500} c={'white'} ff={'monospace'}>
              {wallet?.address}
            </Text>
          </Card>
        </Flex>
        <LineChart data={data} />

        <Card radius="md" padding="xl" bg={theme.colors.dark[8]} w={'100%'}>
          <Text size="lg" tt="uppercase" fw={500} c="pink" ff={'monospace'}>
            Current SUI Price in USD
          </Text>
          <Text fz="xl" fw={500} c={'white'} ff={'monospace'}>
            $0.385649
          </Text>
        </Card>

        <Card radius="md" padding="xl" bg={theme.colors.dark[8]} w={'100%'}>
          <Text size="lg" tt="uppercase" fw={500} c="pink" ff={'monospace'}>
            Objects Available In Your Wallet
          </Text>
          <Text fz="xl" fw={500} c={'white'} ff={'monospace'}>
            {wallet?.contents?.objects.length}
          </Text>
        </Card>

        <Flex>
          <Card radius="md" padding="xl" bg={theme.colors.dark[8]} w={'100%'}>
            <Text size="lg" tt="uppercase" fw={500} c="pink" ff={'monospace'}>
              NFTS Available In Your Wallet
            </Text>
            <Text fz="xl" fw={500} c={'white'} ff={'monospace'}>
              {wallet?.contents?.nfts.length} NFTS
            </Text>
          </Card>
        </Flex>
      </Stack>
    </MainLayout>
  )
}
