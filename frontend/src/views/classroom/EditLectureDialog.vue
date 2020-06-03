<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
    class="stilo"

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


        <div class="videos">
          <ul class="videos__list">
            <li v-for="(video, index) in videos" :key="index" class="videos__item px-5 mb-5">
              <LazyYoutubeVideo
                      :src="video.url"
                      :preview-image-size="video.previewImageSize"
                      :aspect-ratio="video.aspectRatio"


              />
            </li>
          </ul>
        </div>

      </v-card>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click=""
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


Vue.use(ToggleButton);

@Component({
  components: {
    'show-suggestion': ShowSuggestion,
    LazyYoutubeVideo

  },
  data() {
    return {
      videos: [
        {
          url: "https://www.youtube.com/embed/KBMO_4Nj4HQ",
          previewImageSize: "sddefault"
        },
        {
          url: "https://www.youtube.com/embed/65MVwN_Kz1Q",
          previewImageSize: "sddefault"
        },
        {
          url: "https://www.youtube.com/embed/KbX1gYtPVYE",
          previewImageSize: "sddefault",
          aspectRatio: "1:1"
        },
        {
          url: "https://www.youtube.com/embed/etKOc80-cw0",
          previewImageSize: "sddefault"
        }

      ]
    };
  },

})
export default class EditLectureDialog extends Vue {

  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Suggestion, required: true }) readonly lecture!: Suggestion;
  @Prop({ type: String, required: true }) readonly type!: String;

  editLecture!: Suggestion;
  student: User | null = null;
  date: String='';

  videoId : String = '';
  videoBase : String = 'https://www.youtube.com/embed/';




  async created() {
    this.editLecture = new Suggestion(this.lecture);

    this.student = await this.$store.getters.getUser;
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');
    console.log(this.videoId);

  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
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
  .stilo {
    width: 40%;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    padding-left: 0;

    list-style: none;

  }

  .videos {
    &list {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      padding-left: 0;
      margin: {
        top: 0;
        bottom: 15px;
      }
      list-style: none;
    }

    &item {
      width: 30%;
      height: 30%;

      &:nth-child(n + 3) {
        margin-top: $gap;
      }
    }
  }




</style>
