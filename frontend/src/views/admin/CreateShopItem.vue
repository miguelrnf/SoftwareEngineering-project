<template>
  <div class="container">
    <v-card :dark="dark">
      <v-container>
        <p>Create Item</p>
        <v-btn-toggle v-model="toggle" mandatory class="button-group">
          <v-btn value="THEME" text>Theme</v-btn>
          <v-btn value="POWER_UP" text>Power Up</v-btn>
          <v-btn value="POST_AWARD" text>Post Award</v-btn>
        </v-btn-toggle>
      </v-container>
      <v-container v-if="toggle !== ''">
        <v-row>
          <v-col cols="10">
            <v-text-field
              clearable
              v-model="name"
              label="*Name"
              counter
              maxlength="50"
            />
          </v-col>
          <v-col cols="2">
            <v-text-field
              v-model="price"
              hide-details
              single-line
              :rules="[() => price > 0 || 'Price must be positive integer']"
              style="width: 100%"
              type="number"
              suffix="Achandos"
            />
          </v-col>
        </v-row>
        <v-textarea
          v-model="description"
          clearable
          label="*Description"
          counter
          maxlength="250"
          auto-grow
          rows="1"
        />
      </v-container>
      <v-container v-if="toggle === 'THEME'">
        <v-row :justify="'center'">
          <v-col cols="3">
            <v-color-picker
              @input="updateColor"
              :dark="dark"
              mode="hexa"
              hide-mode-switch
              v-model="color"
            />
          </v-col>
          <v-col cols="2">
            <v-switch v-model="dark" label="Dark" />
            <v-card
              class="ma-2"
              :color="primary"
              @click="swatchClick(1, primary)"
              >Primary</v-card
            >
            <v-card
              class="ma-2"
              :color="secondary"
              @click="swatchClick(2, secondary)"
              >Secondary</v-card
            >
            <v-card class="ma-2" :color="accent" @click="swatchClick(3, accent)"
              >Accent</v-card
            >
            <v-card
              class="ma-2"
              :color="warning"
              @click="swatchClick(4, warning)"
              >Warning</v-card
            >
            <v-card class="ma-2" :color="info" @click="swatchClick(5, info)"
              >Info</v-card
            >
            <v-card
              class="ma-2"
              :color="success"
              @click="swatchClick(6, success)"
              >Success</v-card
            >
            <v-card class="ma-2" :color="error" @click="swatchClick(7, error)"
              >Error</v-card
            >
          </v-col>
        </v-row>
        <v-btn @click="createTheme" depressed :color="primary">
          Create item
        </v-btn>
      </v-container>
      <v-container v-if="toggle === 'POWER_UP'">
        <v-card-title>Currently cannot be added</v-card-title>
      </v-container>
      <v-container v-if="toggle === 'POST_AWARD'">
        <v-card-title>To be implemented</v-card-title>
      </v-container>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ShopItem } from '@/models/management/ShopItem';

@Component
export default class CreateShopItem extends Vue {
  toggle: string = '';
  name: string = '';
  content: string = '';
  description: string = '';
  selected: number = 0;
  price: number = 0;
  color: string = '#FFFFFF';
  primary: string = '#FFFFFF';
  secondary: string = '#FFFFFF';
  accent: string = '#FFFFFF';
  warning: string = '#FFFFFF';
  info: string = '#FFFFFF';
  success: string = '#FFFFFF';
  error: string = '#FFFFFF';
  dark: boolean = false;
  shopItem: ShopItem | null = null;

  created() {
    if (this.$vuetify.theme.currentTheme.primary != undefined)
      this.primary = this.$vuetify.theme.currentTheme.primary.toString();
    if (this.$vuetify.theme.currentTheme.secondary != undefined)
      this.secondary = this.$vuetify.theme.currentTheme.secondary.toString();
    if (this.$vuetify.theme.currentTheme.accent != undefined)
      this.accent = this.$vuetify.theme.currentTheme.accent.toString();
    if (this.$vuetify.theme.currentTheme.warning != undefined)
      this.warning = this.$vuetify.theme.currentTheme.warning.toString();
    if (this.$vuetify.theme.currentTheme.info != undefined)
      this.info = this.$vuetify.theme.currentTheme.info.toString();
    if (this.$vuetify.theme.currentTheme.success != undefined)
      this.success = this.$vuetify.theme.currentTheme.success.toString();
    if (this.$vuetify.theme.currentTheme.error != undefined)
      this.error = this.$vuetify.theme.currentTheme.error.toString();
    this.dark = this.$vuetify.theme.dark;
  }

  updateColor() {
    switch (this.selected) {
      case 1:
        this.primary = this.color;
        return;
      case 2:
        this.secondary = this.color;
        return;
      case 3:
        this.accent = this.color;
        return;
      case 4:
        this.warning = this.color;
        return;
      case 5:
        this.info = this.color;
        return;
      case 6:
        this.success = this.color;
        return;
      case 7:
        this.error = this.color;
        return;
    }
  }

  async createTheme() {
    this.shopItem = new ShopItem();
    this.shopItem.description = this.description;
    this.shopItem.price = this.price;
    this.shopItem.name = this.name;
    this.shopItem.type = this.toggle;
    this.shopItem.content =
      this.dark +
      ';' +
      this.success +
      ';' +
      this.secondary +
      ';' +
      this.accent +
      ';' +
      this.info +
      ';' +
      this.warning +
      ';' +
      this.primary +
      ';' +
      this.error;

    try {
      await RemoteServices.createShopTheme(this.shopItem);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  swatchClick(index: number, change: string) {
    this.selected = index;
    this.color = change;
  }
}
</script>

<style lang="scss" scoped>
.message {
  font-weight: bold;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
