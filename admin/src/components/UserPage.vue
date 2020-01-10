<template>
    <a-layout :style="{ marginLeft: '200px' }">
        <a-layout-header :style="{ background: '#fff', padding: 0 }"/>
        <a-layout-content :style="{ margin: '24px 16px 0', overflow: 'initial' }">
            <a-table :columns="columns"
                     :rowKey="record => record.id"
                     :dataSource="userList"
                     size="small">
                <template v-for="col in ['id', 'username', 'age', 'vip']"
                          :slot="col"
                          slot-scope="text, record">
                    <div :key="col">
                        <a-input v-if="record.id == editingModel.id"
                                 style="margin: -5px 0"
                                 :value="editingModel[col]"
                                 @change="e => handleChange(e.target.value, record, col)"/>
                        <template v-else>{{text}}</template>
                    </div>
                </template>
                <template slot="operation" slot-scope="text, record">
                    <div class="editable-row-operations">
                        <span v-if="record.id == editingModel.id">
                            <a-popconfirm title="Sure to save?" @confirm="() => save(record.key)">
                                <a>Save</a>
                            </a-popconfirm>&nbsp;
                            <a @click="() => cancel(record)">Cancel</a>
                        </span>
                        <span v-else>
                            <a @click="() => edit(record)">Edit</a>
                        </span>
                    </div>
                </template>
            </a-table>
        </a-layout-content>
        <a-layout-footer :style="{ textAlign: 'center' }">
            Spring Roll ©2020 Created by Ming Choi
        </a-layout-footer>
    </a-layout>
</template>


<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {State} from "vuex-class";
    import {User} from "@/model/LoginModel";

    @Component
    export default class UserPage extends Vue {

        @State("userList") userList: Array<any>;

        private editingModel: any = {id: undefined};

        columns = [
            {
                title: "ID",
                dataIndex: 'id',
                sorter: true,
                scopedSlots: {customRender: 'id'}
            },
            {
                title: "Username",
                dataIndex: 'username',
                sorter: true,
                scopedSlots: {customRender: 'username'}
            },
            {
                title: "Age",
                dataIndex: 'age',
                sorter: true,
                scopedSlots: {customRender: 'age'}
            },
            {
                title: "VIP",
                dataIndex: 'vip',
                customRender: (text: any, record: any, index: any) =>
                    (text ? "yes" : "no")
            },
            {
                title: 'Operation',
                key: 'operation',
                scopedSlots: {customRender: 'operation'},
            }
        ];

        created() {
            this.$store.dispatch('findAllUser').then(() => {
            });
        }

        /* eslint-disable no-console */
        handleChange(value: any, record: User, column: string) {
            this.editingModel[column] = value;
            this.editingModel = Object.assign({}, this.editingModel);
        }


        edit(record: User) {
            this.editingModel.id = record.id;
            this.editingModel.username = record.username;
            this.editingModel.password = record.password;
            this.editingModel.email = record.email;
            this.editingModel.age = record.age;
            this.editingModel.vip = record.vip;
        }

        save(record: User) {
            this.editingModel.id = record.id;
            // Post to server
            // Pull to list
            console.log(`Saving ${this.editingModel}`);
        }

        cancel(record: User) {
            this.editingModel.id = undefined;
        }
    }
</script>
<style scoped>

</style>