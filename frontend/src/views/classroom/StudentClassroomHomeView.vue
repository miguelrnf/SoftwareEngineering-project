<template>
  <div >
    <v-card
      class="table"
      width="95%"

    >
      <v-row>
      <v-card-title color="primary" class="mb-5">
        <v-icon left>fas fa-chalkboard-teacher</v-icon>
        CLASSROOM
      </v-card-title>
      </v-row>
      <v-tabs >
        <v-tab @click="setTabName('New Lecture')">
          <v-icon left>fab fa-leanpub</v-icon>
          Lectures
        </v-tab >
        <v-tab @click="setTabName('New Lab')">
          <v-icon left>fas fa-laptop-code</v-icon>
          Labs
        </v-tab>
        <v-tab @click="setTabName('New Project')">
          <v-icon left>fab fa-git-alt</v-icon>
          Project
        </v-tab>
        <v-spacer></v-spacer>
        <v-btn color="primary" class="mr-6" v-if="isTeacher()" @click="newLecture">{{tabName}}</v-btn>

        <v-tab-item class="pt-5 pb-10">
          <v-list three-line>
            <v-row>

              <v-col>
                <v-tooltip bottom >
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on" class="mr-2">fas fa-book-open</v-icon>
                  </template>
                  <span>Title</span>
                </v-tooltip>

              </v-col>

              <v-col >
                <v-col>
                  <v-tooltip bottom >
                    <template v-slot:activator="{ on }">
                      <v-icon v-on="on" class="mr-2">far fa-clock</v-icon>
                    </template>
                    <span>Date</span>
                  </v-tooltip>

                </v-col>
              </v-col>
            </v-row>
            <v-list-item-group
                    class="test3"
            >
              <template v-for="(l, index) in lectures">
                <v-list-item :key="l.title" class="test1" @click="showLectureDialog(l)">
                  <template >
                    <v-list-item-content>
                      <v-row>
                        <v-col>
                          <v-list-item-title class="test" v-text=" l.title"></v-list-item-title>

                        </v-col>
                        <v-col>
                          <v-list-item-subtitle class="text--primary" v-text="l.availableDate"></v-list-item-subtitle>
                        </v-col>
                      </v-row>

                    </v-list-item-content>


                  </template>
                </v-list-item>

                <v-divider
                        v-if="index + 1 < lectures.length"
                        :key="index"
                ></v-divider>
              </template>
            </v-list-item-group>
          </v-list>


        </v-tab-item>
        <v-tab-item class="pb-10">
          <v-list three-line>
            <v-row>

              <v-col>
                <v-tooltip bottom >
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on" class="mr-2">fas fa-book-open</v-icon>
                  </template>
                  <span>Title</span>
                </v-tooltip>

              </v-col>

              <v-col >
                <v-col>
                  <v-tooltip bottom >
                    <template v-slot:activator="{ on }">
                      <v-icon v-on="on" class="mr-2">far fa-clock</v-icon>
                    </template>
                    <span>Date</span>
                  </v-tooltip>

                </v-col>
              </v-col>
            </v-row>
            <v-list-item-group
                    class="test3"
            >
              <template v-for="(l, index) in lab">
                <v-list-item :key="l.title" class="test1" @click="showLectureDialog(l)">
                  <template >
                    <v-list-item-content>
                      <v-row>
                        <v-col>
                          <v-list-item-title class="test" v-text=" l.title"></v-list-item-title>

                        </v-col>
                        <v-col>
                          <v-list-item-subtitle class="text--primary" v-text="l.availableDate"></v-list-item-subtitle>
                        </v-col>
                      </v-row>

                    </v-list-item-content>


                  </template>
                </v-list-item>

                <v-divider
                        v-if="index + 1 < lab.length"
                        :key="index"
                ></v-divider>
              </template>
            </v-list-item-group>
          </v-list>


        </v-tab-item>
        <v-tab-item class="pb-10">
          <v-list three-line>
            <v-row>

              <v-col>
                <v-tooltip bottom >
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on" class="mr-2">fas fa-book-open</v-icon>
                  </template>
                  <span>Title</span>
                </v-tooltip>

              </v-col>

              <v-col >
                <v-col>
                  <v-tooltip bottom >
                    <template v-slot:activator="{ on }">
                      <v-icon v-on="on" class="mr-2">far fa-clock</v-icon>
                    </template>
                    <span>Date</span>
                  </v-tooltip>

                </v-col>
              </v-col>
            </v-row>
            <v-list-item-group
                    class="test3"
            >
              <template v-for="(l, index) in project">
                <v-list-item :key="l.title" class="test1" @click="showLectureDialog(l)">
                  <template >
                    <v-list-item-content>
                      <v-row>
                        <v-col>
                          <v-list-item-title class="test" v-text=" l.title"></v-list-item-title>

                        </v-col>
                        <v-col>
                          <v-list-item-subtitle class="text--primary" v-text="l.availableDate"></v-list-item-subtitle>
                        </v-col>
                      </v-row>

                    </v-list-item-content>


                  </template>
                </v-list-item>

                <v-divider
                        v-if="index + 1 < project.length"
                        :key="index"
                ></v-divider>
              </template>
            </v-list-item-group>
          </v-list>

        </v-tab-item>
      </v-tabs>
      <edit-lecture-dialog
              v-if="current && newOrEditDialog"
              v-model="newOrEditDialog"
              :lecture="current"
              :type="tabName"
              :dialog="newOrEditDialog"
              v-on:save-lecture="onSaveLecture"
      />
      <show-lecture-dialog
              v-if="current && showDialog"
              :dialog="showDialog"
              :lecture="current"
              :type="tabName"
              :teacher="isTeacher()"
              v-on:close-show-lecture-dialog="onCloseShowDialog"
      />


    </v-card>
  </div>
</template>

<script lang="ts">
  import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import User from '@/models/user/User';
import Suggestion from '@/models/management/Suggestion';
import EditLectureDialog from '@/views/classroom/EditLectureDialog.vue';
  import Classroom from '@/models/management/Classroom';
  import RemoteServices from '@/services/RemoteServices';
  import ShowLectureDialog from '@/views/classroom/ShowLectureDialog.vue';

@Component({
  components: {
    'edit-lecture-dialog': EditLectureDialog,
    'show-lecture-dialog': ShowLectureDialog,

  }
})
export default class ClassroomHomeView extends Vue {

  @Prop({ type: User, required: false })
  readonly student!: User;

  tabName: string = 'New Lecture';
  students: User[] = [];
  search: string = '';


  lectures: Classroom[] = [];//trocar por lecture
  lab: Classroom[] = [];//trocar por lab
  project: Classroom[] = [];//trocar por project

  newOrEditDialog: boolean = false;


  current: Classroom | null = null;


  showDialog: boolean = false;




  headers: object = [
    {
      text: 'Title',
      align: 'center',
      value: 'title',
      sortable: false
    },

    {
      text: 'Date',
      value: 'availableDate',
      align: 'center'
    },




    { text: 'Status', value: 'status', align: 'center' },

    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    },

  ];

  @Watch('newOrEditDialog')
  closeError() {
    if (!this.newOrEditDialog) {
      this.current = null;
    }
  }


  async created() {
    this.lectures = await RemoteServices.getClassrooms('LECTURE')
    this.lab = await RemoteServices.getClassrooms('LAB')
    this.project= await RemoteServices.getClassrooms('PROJECT')
    console.log(this.lectures)
    console.log(this.lab)
    console.log(this.project)

  }

  setTabName(str: string){
    this.tabName = str;
  }
  getTabName(){
   return this.tabName;
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }

  showLectureDialog(lec: Classroom) {
    this.current = lec;
    this.showDialog = true;
  }

  async onCloseShowDialog() {
    this.lectures = await RemoteServices.getClassrooms('LECTURE')
    this.lab = await RemoteServices.getClassrooms('LAB')
    this.project= await RemoteServices.getClassrooms('PROJECT')
    this.showDialog = false;
  }

  async deleteClassroom(lecture: Classroom) {
    if (
            lecture.id &&
            confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteClassroom(lecture.id);
        if (this.tabName === 'New Lecture') {

          this.lectures = this.lectures.filter(
                  l => l.id != lecture.id
          );

        } else if (this.tabName === 'New Lab') {

          this.lab = this.lab.filter(
                  l => l.id != lecture.id
          );
        } else {

          this.project = this.project.filter(
                  l => l.id != lecture.id
          );
        }


      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  getLectureLabel() {
    if (this.tabName === 'New Lecture') {
      return 'Lecture'
    } else if (this.tabName === 'New Lab') {
      return 'Lab'
    } else {
      return 'Project'
    }
  }
  async changeStatus(lecture: Classroom) {

    this.current = lecture;
    if( this.current.status == 'ACTIVE'){
      this.current.status = 'INACTIVE'
    }else {
      this.current.status = 'ACTIVE'
    }

    console.log(this.current.status)
    this.current = await RemoteServices.changeClassroomStatus(this.current)

    if(this.tabName === 'New Lecture'){
      this.lectures = this.lectures.filter(l => l.id != lecture.id);
      this.lectures.unshift(lecture);
    }
    else if(this.tabName === 'New Lab'){
      this.lab = this.lab.filter(l => l.id != lecture.id);
      this.lab.unshift(lecture);
    }else{
      this.project = this.project.filter(p => p.id != lecture.id);
      this.project.unshift(lecture);
    }

    this.newOrEditDialog = false;
    this.current = null;

  }

  editLecture(lecture: Classroom) {
    this.current = lecture;
    this.newOrEditDialog = true;
  }

  newLecture() {
    this.current = new Classroom();
    this.newOrEditDialog = true;
  }


  async onSaveLecture(lecture: Classroom) {

    if(this.tabName === 'New Lecture'){
      this.lectures = this.lectures.filter(l => l.id != lecture.id);
      this.lectures.unshift(lecture);
    }
    else if(this.tabName === 'New Lab'){
      this.lab = this.lab.filter(l => l.id != lecture.id);
      this.lab.unshift(lecture);
    }else{
      this.project = this.project.filter(p => p.id != lecture.id);
      this.project.unshift(lecture);
    }

    this.newOrEditDialog = false;
    this.current = null;
  }


  customFilter(value: string, search: string, lecture: Suggestion) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
            search != null &&
            JSON.stringify(lecture)
                    .toLowerCase()
                    .indexOf(search.toLowerCase()) !== -1
    );
  }

}
</script>

<style lang="scss" scoped>

  .test{
    font-size: large;

  }

  .test1{
   border-color: #ffffff  ;
    background-color: #FAF6F6  ;
    border-left-style: solid;
    border-right-style: solid;
    border-top-style: solid;

  }
  .test2{
   padding-right: 60px;

  }
  .test3{
    background-color:#FAF6F6 ;

  }



</style>
