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
      <v-tabs>
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

        <v-tab  @click="setTabName('Evaluation Settings')">
          <v-icon left>fas fa-graduation-cap</v-icon>
          Evaluation
        </v-tab>

        <v-spacer></v-spacer>

        <v-btn color="primary" class="mr-6" v-if="isTeacher() && tabName!=='Evaluation Settings'" @click="newLecture">{{tabName}}</v-btn>

        <v-btn color="primary" class="mr-6" v-if="isTeacher() && tabName==='Evaluation Settings'" @click="showEditEvalDialog">{{tabName}}</v-btn>




        <v-tab-item class="pb-10">
          <v-data-table

                  :headers="headers"
                  :custom-filter="customFilter"
                  :items="lectures"
                  :search="search"
                  multi-sort
                  :mobile-breakpoint="0"
                  :items-per-page="15"
                  :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
            <template v-slot:top>
              <v-card-title>
                <v-spacer />
                <v-spacer />
                <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search Lectures"
                        class="mx-2"
                        data-cy="search"
                />



              </v-card-title>
            </template >

            <template v-slot:item.action="{ item }">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="showLectureDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editLecture(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="changeStatus(item)"

                  >fas fa-sync-alt</v-icon
                  >
                </template>
                <span>Change {{getLectureLabel() }} Status</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          color="error"
                          class="mr-2"
                          v-on="on"
                          @click="deleteClassroom(item)"

                  >delete</v-icon
                  >
                </template>
                <span>Delete {{getLectureLabel() }}</span>
              </v-tooltip>
            </template>

          </v-data-table>

        </v-tab-item>
        <v-tab-item class="pb-10">
          <v-data-table
                  :headers="headers"
                  :custom-filter="customFilter"
                  :items="lab"
                  :search="search"
                  multi-sort
                  :mobile-breakpoint="0"
                  :items-per-page="15"
                  :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
            <template v-slot:top>
              <v-card-title>
                <v-spacer />
                <v-spacer />
                <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search Labs"
                        class="mx-2"
                        data-cy="search"
                />



              </v-card-title>
            </template >

            <template v-slot:item.action="{ item }">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="showLectureDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editLecture(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="changeStatus(item)"
                          data-cy="editSuggButton"
                  >fas fa-sync-alt</v-icon
                  >
                </template>
                <span>Change {{getLectureLabel() }} Status</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          color="error"
                          class="mr-2"
                          v-on="on"
                          @click="deleteClassroom(item)"

                  >delete</v-icon
                  >
                </template>
                <span>Delete {{getLectureLabel() }}</span>
              </v-tooltip>
            </template>

          </v-data-table>

        </v-tab-item>
        <v-tab-item class="pb-10">
          <v-data-table
                  :headers="headers"
                  :custom-filter="customFilter"
                  :items="project"
                  :search="search"
                  multi-sort
                  :mobile-breakpoint="0"
                  :items-per-page="15"
                  :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
            <template v-slot:top>
              <v-card-title>
                <v-spacer />
                <v-spacer />
                <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search Projects"
                        class="mx-2"
                        data-cy="search"
                />



              </v-card-title>
            </template >

            <template v-slot:item.action="{ item }">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="showLectureDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editLecture(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
              </v-tooltip>

              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="changeStatus(item)"
                          data-cy="editSuggButton"
                  >fas fa-sync-alt</v-icon
                  >
                </template>
                <span>Change {{getLectureLabel() }} Status</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isTeacher()">
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          color="error"
                          class="mr-2"
                          v-on="on"
                          @click="deleteClassroom(item)"

                  >delete</v-icon>
                </template>
                <span>Delete {{getLectureLabel() }}</span>
              </v-tooltip>

            </template>

          </v-data-table>

        </v-tab-item>

        <v-tab-item>

          <v-data-table
                  :headers="headersStudent"
                  :custom-filter="customFilter"
                  :items="students"
                  :search="search"
                  multi-sort
                  :mobile-breakpoint="0"
                  :items-per-page="15"
                  :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
            <template v-slot:top>
              <v-card-title>
                <v-spacer />
                <v-spacer />
                <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search Students"
                        class="mx-2"
                        data-cy="search"
                />



              </v-card-title>
            </template >

          </v-data-table>

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
      <edit-eval-settings
              v-if="editEvalDialog"
              :dialog="editEvalDialog"
              v-on:close-edit-lecture-dialog="onCloseEditDialog"
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
  import EditEvalSettingsDialog from '@/views/classroom/EditEvalSettingsDialog.vue';
  import {Student} from "@/models/management/Student";

@Component({
  components: {
    'edit-lecture-dialog': EditLectureDialog,
    'show-lecture-dialog': ShowLectureDialog,
    'edit-eval-settings': EditEvalSettingsDialog,

  }
})
export default class ClassroomHomeView extends Vue {

  @Prop({ type: User, required: false })
  readonly student!: User;

  tabName: string = 'New Lecture';
  search: string = '';


  lectures: Classroom[] = [];//trocar por lecture
  lab: Classroom[] = [];//trocar por lab
  project: Classroom[] = [];//trocar por project
  students: Student[] = [];

  newOrEditDialog: boolean = false;


  current: Classroom | null = null;


  showDialog: boolean = false;

  editEvalDialog: boolean = false;




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

  headersStudent: object = [
    {
      text: 'Name',
      align: 'center',
      value: 'name',
      sortable: false
    },
    {
      text: 'Final mark',
      value: 'grade',
      align: 'center'
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
    this.students = await RemoteServices.getEvalStudents();
    console.log(this.lectures)
    console.log(this.lab)
    console.log(this.project)
    console.log(this.students)

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

  showEditEvalDialog(){
    this.editEvalDialog = true;
  }

  async onCloseEditDialog() {
    this.editEvalDialog = false;
    this.students = await RemoteServices.getEvalStudents();
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


  customFilter(value: string, search: string, lecture: Classroom) {
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




</style>
