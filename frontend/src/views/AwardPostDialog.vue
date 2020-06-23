<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-buy-awards-dialog', false)"
    @keydown.esc="$emit('close-buy-awards-dialog', false)"
    max-width="25%"
    max-height="80%"
  >
      <v-card>
      <v-card-title class="justify-center pb-5">Use your available awards</v-card-title>

          <div>
              <v-badge v-if="numSilvers !== 0" :content="'x' + numSilvers" offset-y="62" offset-x="61"><v-icon class="mx-6" color="#AAA9AD" x-large>fas fa-star-half-alt</v-icon></v-badge>
              <v-badge v-if="numGolds !== 0" :content="'x' + numGolds" offset-y="62" offset-x="58"><v-icon  class="mx-5" color="#f9c700" x-large>fas fa-star</v-icon></v-badge>
              <v-badge v-if="numPlatinums !== 0" :content="'x' + numPlatinums" offset-y="62" offset-x="57"><v-icon class="mx-5" color="cyan" x-large>fas fa-shield-alt</v-icon></v-badge>
          </div>

      <v-card-actions class="pt-7 pt-0 px-5">
        <v-spacer />
        <v-btn
          class="mt-2 white--text"
          color="primary"
          @click="$emit('close-buy-awards-dialog')"
          >Cancel</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AwardPostDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  userAwardsInventory: PostAwardItem[] = [];
  numSilvers: number = 0;
  numGolds: number = 0;
  numPlatinums: number = 0;

  async shopRedirect() {
    await this.$router.push({ name: 'shop-home' });
  }

  async getAwards() {
    for (let i = 0; i < this.userAwardsInventory.length; i++) {
      if (this.userAwardsInventory[i].type == 'SILVER') {
        this.numSilvers = this.numSilvers + 1;
      }
      if (this.userAwardsInventory[i].type == 'GOLD') {
        this.numGolds = this.numGolds + 1;
      }
      if (this.userAwardsInventory[i].type == 'PLATINUM') {
        this.numPlatinums = this.numPlatinums + 1;
      }
    }
  }

  async created() {
    this.userAwardsInventory = await RemoteServices.getAwards();
    await this.getAwards();
  }
}
</script>

<style></style>
