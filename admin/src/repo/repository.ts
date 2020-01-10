import {LoginReq, LoginRes, RegisterReq, User} from "../model/LoginModel";

export class Repository {
    static login(creds: LoginReq): Promise<LoginRes> {
        return fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(creds)
        }).then((res) => {
            if (!res.ok) throw Error(`${res.status} ${res.statusText}`);
            return res.json();
        });
    }

    static register(form: RegisterReq): Promise<User> {
        return fetch("/user", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(form)
            }
        ).then((res) => {
            if (!res.ok) throw Error(`${res.status} ${res.statusText}`);
            return res.json();
        })
    }

    static findAllUser(token: String): Promise<Array<User>> {
        return fetch("/user", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                }
            }
        ).then((res) => {
            if (!res.ok) throw Error(`${res.status} ${res.statusText}`);
            return res.json();
        })
    }

}