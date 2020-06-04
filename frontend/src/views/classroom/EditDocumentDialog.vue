<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="60%"



  >
    <v-card class="ma-0 table"  height="60%">
      <v-row>

        <v-card-title color="primary" class="mb-5 table" >
          <v-icon left>fas fa-plus</v-icon>
          {{getDocumentTypeCaps()}}
        </v-card-title>

      </v-row>
      <v-card class="pb-12">
        <div class="pt-5"></div>

          <v-row>
          <v-col class="mx-5">

          </v-col>
          <v-col class="mx-5">

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
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="primary"
          text
          @click="sav"
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
export default class EditDocumentDialog extends Vue {

  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Document, required: true }) readonly document!: Document;
  @Prop({ type: String, required: true }) readonly type!: String;

  editDocument!: Document;
  date!: string ;

  videoId : String = '';
  videoBase : String = 'https://www.youtube.com/embed/';




  async created() {
    this.editDocument = new Document();
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');

  }


  getDocumentTypeCaps() {
    if(this.type === 'New Document'){
      return 'DOCUMENT'
    } else {
      return 'VIDEO'
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





}
</script>
<style lang="scss" scoped>
  $gap: 20px;


  .testttt{
    background: #761515;
    position: absolute;
    top: 50px;
  }

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
      width: 30px;
      height: 30px;

      &:nth-child(n + 3) {
        margin-top: $gap;
      }
    }
  }




</style>
