<template>
  <v-card class="mx-auto mt-4" width="95%" flat v-scroll height="95%">
    <v-row>
      <v-col cols="3">
        <v-navigation-drawer absolute permanent dense color="primary">
          <v-row class="white--text mt-5 ml-4 headline">
            <v-icon color="white" class="mr-12">fas fa-align-justify</v-icon>
            {{ 'Categories' }}
          </v-row>
          <v-divider class="mt-5 white"></v-divider>
          <v-list>
            <v-list-item
              v-for="item in categories"
              :key="item.title"
              @click="defineCategory(item.value)"
            >
              <v-list-item-icon>
                <v-icon color="white">{{ item.icon }}</v-icon>
              </v-list-item-icon>

              <v-list-item-content class="white--text">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>

          <template v-slot:append>
            <v-row class="white--text pa-5 ml-0" justify="center">
              {{ numHint + 'x' }}
              <v-icon color="white" class="mr-4">far fa-lightbulb</v-icon>

              {{ numFifty + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-adjust</v-icon>

              {{ numRightAns + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-check</v-icon>
            </v-row>
            <v-row class="white--text pa-5 ml-0">
              <v-icon color="white" class="mr-12">fas fa-coins</v-icon>
              {{ $store.getters.getUser.score + ' Achandos' }}
            </v-row>
          </template>
        </v-navigation-drawer>
      </v-col>
      <v-col cols="9">
        <v-container style="max-height: 87vh" class="overflow-y-auto" fluid>
          <div>
            <v-row>
              <v-col cols="4" v-for="item in filteredItems" :key="item.name">
                <v-card outlined tile class="ma-3">
                  <v-list-item three-line>
                    <v-list-item-content>
                      <v-list-item-title class="text-left">
                        {{ item.name }}
                      </v-list-item-title>
                      <v-list-item-subtitle class="text-left">
                        {{ item.price + ' Achandos' }}
                      </v-list-item-subtitle>
                    </v-list-item-content>

                    <v-icon
                      v-if="item.type !== 'THEME'"
                      x-large
                      :color="item.color"
                      class="ma-4"
                    >
                      {{ item.icon }}
                    </v-icon>
                    <v-icon
                      v-else
                      x-large
                      class="ma-4"
                      @click="openDialog(item)"
                    >
                      fas fa-eye
                    </v-icon>
                  </v-list-item>

                  <v-card-actions class="px-4">
                    <v-btn
                      right
                      color="primary"
                      @click="buyItem(item)"
                      :disabled="hasItem(item)"
                      :key="reload"
                    >
                      Buy
                    </v-btn>
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon class="ma-4 pa-0" v-on="on"
                          >far fa-question-circle</v-icon
                        >
                      </template>
                      <span>{{ item.description }}</span>
                    </v-tooltip>
                  </v-card-actions>
                </v-card>
              </v-col>
            </v-row>
          </div>
        </v-container>
      </v-col>
    </v-row>
    <theme-preview
      :theme="currentTheme"
      :dialog="themeDialog"
      v-on:close-show-theme-dialog="themeDialog = false"
    />
    <v-dialog v-model="show" hide-overlay persistent width="300">
      <v-card
        style=" position: absolute; top: 9%"
        max-width="20%"
        color="primary"
      >
        <v-card-text
          style="font-size: large; font-weight: bold"
          class="white--text py-3"
        >
          Wait a moment
          <v-progress-linear
            indeterminate
            color="white"
            class="ma-0 mt-2"
          ></v-progress-linear>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog
      v-model="show2"
      retain-focus
      transition="dialog-bottom-transition"
      hide-overlay
      width="300"
    >
      <v-card
        elevation="5"
        class="pa-2"
        style=" position: absolute; top: 9%"
        max-width="20%"
        color="success darken-2"
      >
        <v-card-text
          style="font-size: x-large; font-weight: bold"
          class="white--text pt-3"
        >
          Thank you for shopping with us!
        </v-card-text>

        <v-card-text style="font-size: medium" class="white--text">
          Your purchase of {{ last }} <br />
          was successfully processed and <br />
          is now ready to be used! <br />
        </v-card-text>
        <v-btn dark text @click="show2 = false">Close</v-btn>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import { ShopItem } from '@/models/management/ShopItem';
import RemoteServices from '@/services/RemoteServices';
import ThemePreviewDialog from '@/views/ThemePreviewDialog.vue';
import { Theme } from '@/models/management/Theme';

@Component({
  components: {
    'theme-preview': ThemePreviewDialog
  }
})
export default class ShopHomeView extends Vue {
  search: string = '';
  type: string = 'None';
  numHint: number = 0;
  numFifty: number = 0;
  numRightAns: number = 0;
  show: boolean = false;
  show2: boolean = false;
  last: string = '';
  currentTheme: Theme = new Theme();
  themeDialog: boolean = false;
  colors: string[] | undefined;
  themes: string[] | undefined;
  reload: number = 0;

  categories = [
    { value: 'THEME', title: 'Themes', icon: 'fas fa-paint-roller' },
    { value: 'POWER_UP', title: 'Power Ups', icon: 'fas fa-rocket' },
    { value: 'POST_AWARD', title: 'Post Awards', icon: 'fas fa-star' },
    { value: 'All', title: 'All', icon: 'fas fa-ellipsis-v' }
  ];

  filteredItems: ShopItem[] = [];
  shopItems: ShopItem[] = [];

  loader() {
    setTimeout(() => ((this.show = false), (this.show2 = true)), 1500);
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.themes = await RemoteServices.getOwnedThemes();
      this.shopItems = await RemoteServices.getShopItems();
      this.shopItems.sort((a, b): number => this.sortfn(a, b));
      this.filteredItems = this.shopItems;
      this.numRightAns = await RemoteServices.getNumOfPowerUp('RIGHTANSWER');
      this.numHint = await RemoteServices.getNumOfPowerUp('HINT');
      this.numFifty = await RemoteServices.getNumOfPowerUp('FIFTYFIFTY');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  sortfn(a: ShopItem, b: ShopItem): number {
    if (a.type === b.type) {
      return a.price < b.price ? -1 : a.price == b.price ? 0 : 1;
    }
    return a.type < b.type ? 1 : -1;
  }

  async buyItem(item: ShopItem) {
    await this.$store.dispatch('loading');
    try {
      this.last = item.name;
      this.show = true;
      await RemoteServices.buyItem(item.id);
      this.loader();
      await this.$store.dispatch('updateUser');
      this.numRightAns = await RemoteServices.getNumOfPowerUp('RIGHTANSWER');
      this.numHint = await RemoteServices.getNumOfPowerUp('HINT');
      this.numFifty = await RemoteServices.getNumOfPowerUp('FIFTYFIFTY');
      this.themes = await RemoteServices.getOwnedThemes();
      this.reload += 1;
    } catch (error) {
      this.show2 = false;
      this.show = false;
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  hasItem(item: ShopItem): boolean {
    if (item.type != 'THEME') return false;
    if (this.themes) return this.themes?.includes(item.name);
    return false;
  }

  openDialog(item: ShopItem) {
    this.setTheme(item);

    this.themeDialog = true;
  }

  setTheme(item: ShopItem) {
    this.colors = item.content.split(';');

    this.currentTheme.dark = this.colors[0] === 'true';
    this.currentTheme.success = this.colors[1];
    this.currentTheme.secondary = this.colors[2];
    this.currentTheme.accent = this.colors[3];
    this.currentTheme.info = this.colors[4];
    this.currentTheme.warning = this.colors[5];
    this.currentTheme.primary = this.colors[6];
    this.currentTheme.error = this.colors[7];
  }

  defineCategory(type: string) {
    this.type = type;
    this.isInTheRightCategory(type);
  }

  async isInTheRightCategory(search: string) {
    if (search != '' && search != 'All') {
      this.filteredItems = this.shopItems.filter(
        item => item.type == this.type
      );
    } else {
      this.filteredItems = this.shopItems;
    }
    return this.filteredItems;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
