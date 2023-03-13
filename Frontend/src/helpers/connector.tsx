import axios from 'axios';
import { wrapper } from 'axios-cookiejar-support';
import{ CookieJar } from 'tough-cookie';

const jar = new CookieJar();
const client = wrapper(axios.create( {jar} ));

var host = "http://localhost:8080";

var url = host + "/api/v1/";

export async function register(email: string, password: string, name: string, dob: string){
    let content = await client({
        method: 'post',
        url: url+"register",
        data:{
            email: email,
            password: password,
            name: name,
            dob: dob,
        }
    });
    return content;
}

export async function login(email: string, password: string){
    let content = await client({
        method: 'post',
        url: url+"login",
        data:{
            email: email,
            password: password,
        }
    });
    return content;
}


export async function logout(){
    let content = await client({
        method: 'post',
        url: url+"logout",
        withCredentials: true
    });
    return content;
}
export async function getWordAndDef(inputWord: string): Promise<String[]>{
    let content = await client({
        method: 'get',
        url: url+"getWordAndDef",
        params: {
          inputWord: inputWord
        },
        withCredentials: true
    });
    return content.data;
}

export async function getRandomStart(): Promise<String>{
    let content = await client({
        method: 'get',
        url: url+"getRandomStart",
        withCredentials: true
    });
    return content.data;
}


export async function isWordExist(inputWord: string): Promise<boolean>{
    let content = await client({
        method: 'get',
        url: url+"isWordExist",
        params: {
          inputWord: inputWord
        },
        withCredentials: true
    });
    return content.data.exist;
}

export async function getLetterFromPreviousWord(inputWord: string): Promise<String>{
    let content = await client({
        method: 'get',
        url: url+"getLetterFromPreviousWord",
        params: {
          inputWord: inputWord
        },
        withCredentials: true
    });
    return content.data;
}

export async function getHintWordAndDef(inputWordLetter: string): Promise<String[]>{
    let content = await client({
        method: 'get',
        url: url+"getHintWordAndDef",
        params: {
            inputWordLetter: inputWordLetter
        },
        withCredentials: true
    });
    return content.data;
}

export async function getBestScore(){
    let content = await client({
        method: 'get',
        url: url+"getBestScore"
    });
    return content.data;
}

export async function updateBestScore(currentScore: number): Promise<number[][]>{
    let content = await client({
        method: 'get',
        url: url+"updateBestScore",
        params: {
            currentScore: currentScore
        },
    });
    return content.data;
}



