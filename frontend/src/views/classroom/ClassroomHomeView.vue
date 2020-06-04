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
                        label="Search"
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
                          @click="showSuggestionDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editSuggestion(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
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
                        label="Search"
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
                          @click="showSuggestionDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editSuggestion(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
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
                        label="Search"
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
                          @click="showSuggestionDialog(item)"
                          data-cy="showLectureButton"
                  >visibility</v-icon
                  >
                </template>
                <span>Show {{getLectureLabel()}}</span>
              </v-tooltip>
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          small
                          class="mr-2"
                          v-on="on"
                          @click="editSuggestion(item)"
                          data-cy="editSuggButton"
                  >edit</v-icon
                  >
                </template>
                <span>Edit {{getLectureLabel()}}</span>
              </v-tooltip>

            </template>

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

@Component({
  components: {
    'edit-lecture-dialog': EditLectureDialog,

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
      text: 'Class Date',
      value: 'availableDate',
      align: 'center'
    },

    {
      text: 'Summary',
      align: 'center',
      value: 'title',
      sortable: false
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

  }

  setTabName(str: string){
    this.tabName = str;
    console.log(this.tabName);
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

  onCloseShowDialog() {
    this.showDialog = false;
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
  editSuggestion(lecture: Classroom) {
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




</style>