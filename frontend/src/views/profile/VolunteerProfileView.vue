<template>
  <div class="container">
    <!-- Button to Create Volunteer Profile -->
    <div class="horizontal-btn-container" v-if="!this.profile.id">
      <h1>Volunteer Profile</h1>
      <div class="no-profile-message">
        <p>No volunteer profile found. Click the button below to create a new one!</p>
      </div>
      <v-btn depressed @click="createProfile" color="blue darken-2" data-cy="userLoginButton"  class="white--text">
        CREATE MY PROFILE
      </v-btn>
    </div>
    <div v-else>
      <h1>Volunteer: {{ this.profile.volunteer?.name }}</h1>
      <div class="text-description">
        <p><strong>Short Bio: </strong> {{ this.profile.shortBio }}</p>
      </div>
      <div class="stats-container">
        <!-- Total Enrollments -->
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ this.profile.numTotalEnrollments }}</span>
          </div>
          <div class="project-name">
            <p>Total Enrollments</p>
          </div>
        </div>

        <!-- Total Participations -->
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ this.profile.numTotalParticipations }}</span>
          </div>
          <div class="project-name">
            <p>Total Participations</p>
          </div>
        </div>

        <!-- Total Assessments -->
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ this.profile.numTotalAssessments }}</span>
          </div>
          <div class="project-name">
            <p>Total Assessments</p>
          </div>
        </div>

        <!-- Average Rating -->
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ this.profile.averageRating }}</span>
          </div>
          <div class="project-name">
            <p>Average Rating</p>
          </div>
        </div>
      </div>

      <div>
        <h2>Selected Participations</h2>
        <div>
          <v-card class="table">
            <v-data-table :headers="headers" :search="search" disable-pagination :hide-default-footer="true"
              :mobile-breakpoint="0">
              <template v-slot:item.activityName="{ item }">
                {{ activityName(item) }}
              </template>
              <template v-slot:item.institutionName="{ item }">
                {{ institutionName(item) }}
              </template>
              <template v-slot:item.memberRating="{ item }">
                {{ getMemberRating(item) }}
              </template>
              <template v-slot:top>
                <v-card-title>
                  <v-text-field v-model="search" append-icon="search" label="Search" class="mx-2" />
                  <v-spacer />
                </v-card-title>
              </template>
            </v-data-table>
          </v-card>
        </div>
      </div>
    </div>

      <volunteerProfileDialog
        v-if="volunteerProfileDialog"
        v-model="volunteerProfileDialog"
        :volunteer="newProfile"
        @close-profile-dialog="onCloseProfileDialog"
        @create-profile-dialog="onCreateProfileDialog"
      />

  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Participation from '@/models/participation/Participation';
import Activity from '@/models/activity/Activity';
import VolunteerProfile from '@/models/volunteer/VolunteerProfile';
import Volunteer from '@/models/volunteer/Volunteer';
import VolunteerProfileDialog from '@/views/profile/VolunteerProfileDialog.vue';

@Component({
  components: {
    'volunteerProfileDialog': VolunteerProfileDialog
  },
})
export default class VolunteerProfileView extends Vue {
  userId: number = 0;

  activities: Activity[] = [];
  profile: VolunteerProfile = new VolunteerProfile();

  volunteerProfileDialog: boolean = false;
  private newProfile: VolunteerProfile = new VolunteerProfile();

  search: string = '';
  headers: object = [
    {
      text: 'Activity Name',
      value: 'activityName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Rating',
      value: 'memberRating',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Review',
      value: 'memberReview',
      align: 'left',
      width: '40%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');

    try {
      this.userId = Number(this.$route.params.id);
      this.activities = await RemoteServices.getActivities();
      // TODO
      this.profile = await RemoteServices.getVolunteerProfile(this.userId);
      console.log("ID:  ", this.profile.id);
      if (!this.profile.id) {
        // Volunteer
        this.profile.volunteer = new Volunteer();
        this.profile.volunteer.id = this.userId;

        this.profile.shortBio = 'This is a short bio';

        this.profile.selectedParticipations = [];
        // TODO: esta a ficar preso ao criar o perfil
        // console.log('Creating new profile');
        // console.log(await RemoteServices.createVolunteerProfile(this.profile));
        // console.log('Profile created');
        // this.profile = await RemoteServices.getVolunteerProfile(this.userId);
        // console.log(this.profile);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async onCreateProfileDialog() {
    await this.$store.dispatch('loading');
    try {
      // Chama o serviço para criar o perfil
      const createdProfile = await RemoteServices.createVolunteerProfile(this.newProfile);
      this.profile = createdProfile;
      this.volunteerProfileDialog = false;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  createProfile() {
    this.newProfile = new VolunteerProfile();
    this.newProfile.volunteer = new Volunteer();
    this.newProfile.volunteer.id = this.userId;
    this.newProfile.selectedParticipations = [];
    this.volunteerProfileDialog = true;
  }

  activityName(participation: Participation) {
    return this.activities.find(
      (activity) => activity.id == participation.activityId,
    )?.name;
  }

  institutionName(participation: Participation) {
    let activity = this.activities.find(
      (activity) => activity.id == participation.activityId,
    );
    return activity?.institution.name;
  }

  getMemberRating(participation: Participation): string {
    if (!participation || participation.memberRating == null) {
      return '';
    }
    return this.convertToStars(participation.memberRating);
  }

  convertToStars(rating: number): string {
    const fullStars = '★'.repeat(Math.floor(rating));
    const emptyStars = '☆'.repeat(Math.floor(5 - rating));
    return `${fullStars}${emptyStars} ${rating}/5`;
  }

  onCloseProfileDialog() {
    this.volunteerProfileDialog = false;
    // TODO: Check if needs anything else to be reseted
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #696969;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}

.no-profile-message {
  display: block;
  padding: 1em;
}
</style>