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
                <span>Show {{tabName}}</span>
              </v-tooltip>
            </template>

          </v-data-table>

        </v-tab-item>
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
                <span>Show {{tabName}}</span>
              </v-tooltip>
            </template>

          </v-data-table>

        </v-tab-item>
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
                <span>Show {{tabName}}</span>
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
import { Component, Prop, Vue } from 'vue-property-decorator';
import User from '@/models/user/User';
import Suggestion from '@/models/management/Suggestion';
import EditLectureDialog from '@/views/classroom/EditLectureDialog.vue';

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


  lectures: Suggestion[] = [];//trocar por lecture
  lab: Suggestion[] = [];//trocar por lab
  project: Suggestion[] = [];//trocar por project

  newOrEditDialog: boolean = false;


  current: Suggestion | null = null;


  showDialog: boolean = false;




  headers: object = [
    {
      text: 'Class Date',
      value: 'creationDate',
      align: 'center'
    },

    {
      text: 'Summary',
      align: 'center',
      sortable: false
    },


    { text: 'Visability', value: 'status', align: 'center' },

    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    },

  ];




  async created() {
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

  showLectureDialog(sugg: Suggestion) {
    this.current = sugg;
    this.showDialog = true;
  }

  onCloseShowDialog() {
    this.showDialog = false;
  }

  newLecture() {
    this.current = new Suggestion();
    this.newOrEditDialog = true;
    console.log(this.tabName)
  }

  async onSaveLecture(sugg: Suggestion) {
    if(this.tabName === 'New Lecture'){
      this.lectures.unshift(sugg);
    }
    else if(this.tabName === 'New Lab'){
      this.lab.unshift(sugg);
    }else{
      this.project.unshift(sugg);
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
