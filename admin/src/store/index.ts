import Vue from "vue";
import Vuex from "vuex";
import {LoginReq, LoginRes, RegisterReq, User} from "@/model/LoginModel";
import {Repository} from "@/repo/repository";

Vue.use(Vuex);

/* eslint-disable no-console */
export default new Vuex.Store({
    state: {
        errorMessage: "",
        token: "",
        user: {},
        userList: Array<User>()
    },
    mutations: {
        setErrorMessage: (state, msg: string) => {
            state.errorMessage = msg;
        },
        saveLogonInfo: (state, res: LoginRes) => {
            state.token = res.token;
            state.user = res.user;
        },
        saveCreatedUser: (state, user: User) => {
            state.user = user;
        },
        updateUserList: (state, list: Array<User>) => {
            state.userList = list;
        }
    },
    actions: {
        login: (ctx, creds: LoginReq) => {
            return new Promise((resolve, reject) => {
                Repository.login(creds)
                    .then((data: LoginRes) => {
                        ctx.commit("saveLogonInfo", data);
                        resolve();
                    })
                    .catch(reason => reject(reason));
            });
        },
        register: (ctx, form: RegisterReq) => {
            return new Promise((resolve, reject) => {
                Repository.register(form)
                    .then((user: User) => {
                        ctx.commit("saveCreatedUser", user)
                        resolve();
                    })
                    .catch(reason => reject(reason));
            });

        },
        findAllUser: (ctx) => {
            return new Promise((resolve, reject) => {
                Repository.findAllUser(ctx.state.token)
                    .then((list: Array<User>) => {
                        ctx.commit("updateUserList", list);
                        resolve();
                    })
                    .catch(reason => reject(reason));
            });
        }
    },
    modules: {}
})
/* eslint-enable no-console */
