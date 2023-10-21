import {
  Box,
  Button,
  FileInput,
  Image,
  Stack,
  TextInput,
  Title,
} from '@mantine/core'
import { upload } from '@spheron/browser-upload'
import { ethos, TransactionBlock } from 'ethos-connect'
import { useRouter } from 'next/router'
import { ChangeEvent, useCallback, useEffect, useState } from 'react'
import { MainLayout } from '../../layout/MainLayout'
import { ETHOS_EXAMPLE_CONTRACT } from '../../lib/constants'

export default function MintNFTPage() {
  const [file, setFile] = useState<File | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [uploadLink, setUploadLink] = useState<string>('')
  const [dynamicLink, setDynamicLink] = useState<string>('')
  const [nftName, setNftName] = useState<string>('')
  const [nftDescription, setNftDescription] = useState<string>('')
  const router = useRouter()

  const { wallet } = ethos.useWallet()
  const [nftObjectId, setNftObjectId] = useState<string | undefined>()

  const handleUpload = async () => {
    if (!file) {
      alert('No file selected')
      return
    }

    try {
      setIsLoading(true)
      const response = await fetch('http://localhost:8111/initiate-upload')
      const responseJson = await response.json()
      const uploadResult = await upload([file], {
        token: responseJson.uploadToken,
      })

      setUploadLink(uploadResult.protocolLink)
      setDynamicLink(uploadResult.dynamicLinks[0])
    } catch (err) {
      alert(err)
    } finally {
      setIsLoading(false)
    }
  }

  const mint = useCallback(async () => {
    if (!wallet?.currentAccount) return

    handleUpload()
      .then(async () => {
        try {
          setTimeout(() => {
            console.log(uploadLink + '/' + dynamicLink)
          }, 10000)

          const transactionBlock = new TransactionBlock()
          transactionBlock.moveCall({
            target: `${ETHOS_EXAMPLE_CONTRACT}::ethos_example_nft::mint_to_sender`,
            arguments: [
              transactionBlock.pure(nftName),
              transactionBlock.pure(nftDescription),
              transactionBlock.pure(
                process.env.NEXT_PUBLIC_IPFS_GATEWAY + '/' + file?.name,
              ),
            ],
          })
          const response = await wallet.signAndExecuteTransactionBlock({
            transactionBlock,
            options: {
              showObjectChanges: true,
            },
          })
          if (response?.objectChanges) {
            const createdObject = response.objectChanges.find(
              (e) => e.type === 'created',
            )
            if (createdObject && 'objectId' in createdObject) {
              setNftObjectId(createdObject.objectId)
            }
          }
        } catch (error) {
          console.log(error)
        }
      })
      .catch((err) => {
        console.log(err)
      })
  }, [wallet, nftName, nftDescription, dynamicLink, handleUpload])

  const reset = useCallback(() => {
    setNftObjectId(undefined)
  }, [])

  useEffect(() => {
    reset()
  }, [reset])

  useEffect(() => {
    if (nftObjectId) {
      router.push('/app/my-nfts')
    }
  }, [nftObjectId])

  return (
    <MainLayout>
      <Stack maw={500} mx={'auto'} mt={40} gap={30}>
        <Title order={2}>Mint NFT</Title>
        <TextInput
          description="Enter a name for your NFT"
          label="Name"
          onChange={(e: ChangeEvent<HTMLInputElement>) =>
            setNftName(e.target.value)
          }
        />
        <TextInput
          description="Enter a description for your NFT"
          label="Description"
          onChange={(e: ChangeEvent<HTMLInputElement>) =>
            setNftDescription(e.target.value)
          }
        />

        <FileInput
          description="Upload an image to mint a new NFT. The image will be uploaded to IPFS and the resulting link will be stored on the blockchain."
          label="Upload an image"
          value={file}
          onChange={setFile}
        />
        {file && (
          <Box w={300} h={300}>
            <Image
              src={URL.createObjectURL(file)}
              alt="Uploaded image"
              style={{ width: '100%', height: '200' }}
            />
          </Box>
        )}
        <Button
          onClick={mint}
          loading={isLoading}
          disabled={!file || !nftName || !nftDescription}
        >
          Create My NFT
        </Button>
      </Stack>

      {dynamicLink + '/' + uploadLink}
    </MainLayout>
  )
}
