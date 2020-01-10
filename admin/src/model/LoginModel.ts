export interface User {
    id: string;
    username: string;
    password: string;
    email: string;
    age: number;
    vip: boolean;
}

export interface LoginReq {
    username: string;
    password: string;
}

export interface LoginRes {
    user: User;
    token: string;
}

export interface RegisterReq {
    username: string;
    password: string;
    email: string;
    age: number;
    vip: boolean;
}