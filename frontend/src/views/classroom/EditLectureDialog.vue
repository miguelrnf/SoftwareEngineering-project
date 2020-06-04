<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"


  >
    <v-card>
      <v-app-bar dense color="primary">
        <v-toolbar-title class="white--text" v-if="type === 'New Lecture'">{{
          editLecture && editLecture.id === null
            ? 'New Lecture'
            : 'Edit Lecture'
        }}</v-toolbar-title>
        <v-toolbar-title class="white--text" v-else-if="type === 'New Lab'">{{
          editLecture && editLecture.id === null
          ? 'New Lab'
          : 'Edit Lab'
          }}</v-toolbar-title>
        <v-toolbar-title class="white--text" v-else>{{
          editLecture && editLecture.id === null
          ? 'New Project'
          : 'Edit Project'
          }}</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-app-bar>
      <v-card class="pb-12">
        <div class="pt-5"></div>

          <v-row>
          <v-col class="mx-5">
            <v-textarea

                    :label=getLectureLabel()
                    v-model="editLecture.title"
                    auto-grow
                    outlined
                    rows="1"
                    row-height="15"

            ></v-textarea>
          </v-col>
          <v-col class="mx-5">
            <VueCtkDateTimePicker

                    :label=getLectureDateLabel()
                    id="availableDateInput"
                    v-model="date"
                    format="YYYY-MM-DDTHH:mm:ssZ"
                    data-cy="availableDate"

            >
            </VueCtkDateTimePicker>
          </v-col>

          </v-row>


        <div >

            <v-container class="test">
              <LazyYoutubeVideo
                      :src="this.videoBase"
                      :previewImageSize="sddefault"
              />
              </v-container>


        </div>

      </v-card>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="primary"
          text
          @click="saveLecture"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Suggestion from '@/models/management/Suggestion';
import Topic from '@/models/management/Topic';
import ToggleButton from 'vue-js-toggle-button';
import User from '@/models/user/User';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ShowSuggestion from '@/views/ShowSuggestion.vue';
import VueYouTubeEmbed, { getIdFromURL } from 'vue-youtube-embed';
import LazyYoutubeVideo from 'vue-lazy-youtube-video'
import 'vue-lazy-youtube-video/dist/style.css'
import Classroom from '@/models/management/Classroom';


Vue.use(ToggleButton);

@Component({
  components: {
    LazyYoutubeVideo

  },
  data() {
    return {
      videos: [
        {
          url: "https://www.youtube.com/embed/KBMO_4Nj4HQ",
          previewImageSize: "sddefault"
        },

      ]
    };
  },

})
export default class EditLectureDialog extends Vue {

  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;
  @Prop({ type: String, required: true }) readonly type!: String;

  editLecture!: Classroom;
  student: User | null = null;
  date!: string ;

  videoId : String = '';
  videoBase : String = 'https://www.youtube.com/embed/KBMO_4Nj4HQ';




  async created() {
    this.editLecture = new Classroom(this.lecture);

    this.student = await this.$store.getters.getUser;
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');

  }



  getLectureType() {
    if (this.type === 'New Lecture') {
      return 'Lecture'
    } else if (this.type === 'New Lab') {
      return 'Lab'
    } else {
      return 'Project'
    }
  }

  getLectureDateLabel() {
    if (this.type === 'New Lecture') {
      return 'Date Of Class Of Type Lecture'
    } else if (this.type === 'New Lab') {
      return 'Date Of Class Of Type Lab'
    } else {
      return 'Date Of Class Of Type Project'
    }
  }

  async saveLecture() {
    this.editLecture.availableDate = this.date;

    if (this.editLecture && this.editLecture.id != null) {

      try {
        const result = await RemoteServices.editClassroom(
                this.editLecture
        );
        this.$emit('save-lecture', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editLecture) {
      try {

        if (this.type === 'New Lecture') {
          this.editLecture.type = 'LECTURE'
        } else if (this.type === 'New Lab') {
          this.editLecture.type = 'LAB'
        } else {
          this.editLecture.type = 'PROJECT'
        }


        this.editLecture.status='INACTIVE'

        const result = await RemoteServices.createClassroom(
                this.editLecture
        );
        this.$emit('save-lecture', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }



  getLectureLabel() {
    if (this.type === 'New Lecture') {
      return 'Write Lecture Summary Here'
    } else if (this.type === 'New Lab') {
      return 'Write Lab Summary Here'
    } else {
      return 'Write Project Summary Here'
    }
  }


}
</script>
<style lang="scss" scoped>
  $gap: 20px;
  .test {
    width: 40%;
    alignment: left;

  }






</style>
