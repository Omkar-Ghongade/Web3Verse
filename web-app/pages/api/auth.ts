import type { NextApiRequest, NextApiResponse } from 'next'

type ResponseData = {
  message: string
}

export default async function handler(
  request: NextApiRequest,
  response: NextApiResponse<ResponseData>,
) {
  const requestData = {
    jwt: request.query['jwt'],
  }

  try {
    // First Request
    const myHeaders1 = new Headers()
    myHeaders1.append('Content-Type', 'application/json')
    const raw1 = JSON.stringify({
      token: requestData.jwt,
    })
    const requestOptions1: RequestInit = {
      method: 'POST',
      headers: myHeaders1,
      body: raw1,
      redirect: 'follow',
    }
    const saltResponse = await fetch(
      'http://salt.api-devnet.mystenlabs.com/get_salt',
      requestOptions1,
    )
    const saltResult = await saltResponse.json()
    console.log(saltResult)
    response.status(200).json(saltResult)
    console.log('Response has been sent')
  } catch (error) {
    console.error(error)
    // @ts-ignore
    response.status(error.code || 500).send(error)
  }
}
