<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card class="px-12 pt-5 ">
      <v-row>
        <v-card-title color="primary" class="mb-5 ">
          <v-icon left>{{ getLectureTypeIcon() }}</v-icon>
          {{ getLectureTypeCaps() }}
        </v-card-title>
      </v-row>
      <v-row>
        <v-textarea
          :label="getLectureLabel()"
          v-model="editLecture.title"
          auto-grow
          outlined
          rows="20"
          row-height="15"
        ></v-textarea>
      </v-row>
      <v-row>
        <VueCtkDateTimePicker
          :label="getLectureDateLabel()"
          id="availableDateInput"
          v-model="date"
          format="YYYY-MM-DDTHH:mm:ssZ"
          data-cy="availableDate"
        >
        </VueCtkDateTimePicker>
      </v-row>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn color="primary" text @click="saveLecture" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ToggleButton from 'vue-js-toggle-button';
import User from '@/models/user/User';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import { getIdFromURL } from 'vue-youtube-embed';
import LazyYoutubeVideo from 'vue-lazy-youtube-video';
import 'vue-lazy-youtube-video/dist/style.css';
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
          url: 'https://www.youtube.com/embed/KBMO_4Nj4HQ',
          previewImageSize: 'sddefault'
        }
      ]
    };
  }
})
export default class EditLectureDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;
  @Prop({ type: String, required: true }) readonly type!: String;

  editLecture!: Classroom;
  student: User | null = null;
  date: String | null = null;

  videoId: String = '';

  async created() {
    this.editLecture = new Classroom(this.lecture);

    this.student = await this.$store.getters.getUser;
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');
  }

  getLectureDateLabel() {
    return 'Class Date';
  }
  getLectureTypeCaps() {
    if (this.type === 'New Lecture') {
      return 'LECTURE';
    } else if (this.type === 'New Lab') {
      return 'LAB';
    } else {
      return 'PROJECT';
    }
  }

  async saveLecture() {
    this.editLecture.availableDate = this.date;

    if (this.editLecture && this.editLecture.id != null) {
      try {
        const result = await RemoteServices.editClassroom(this.editLecture);
        this.$emit('save-lecture', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editLecture) {
      try {
        if (this.type === 'New Lecture') {
          this.editLecture.type = 'LECTURE';
        } else if (this.type === 'New Lab') {
          this.editLecture.type = 'LAB';
        } else {
          this.editLecture.type = 'PROJECT';
        }

        this.editLecture.status = 'INACTIVE';

        const result = await RemoteServices.createClassroom(this.editLecture);
        this.$emit('save-lecture', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  getLectureTypeIcon() {
    if (this.editLecture && this.editLecture.id === null) {
      return 'fas fa-plus';
    } else {
      return 'edit';
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getLectureLabel() {
    if (this.type === 'New Lecture') {
      return 'Write Lecture Title Here';
    } else if (this.type === 'New Lab') {
      return 'Write Lab Title Here';
    } else {
      return 'Write Project Title Here';
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
