<template>
  <div style="display: flex; flex-direction: column; height: 100vh">
    <div style="display: flex; justify-content: center; align-items: center; flex: 3;">
      <div id="map" style="width:90%; height:90%; border-radius: 8px"></div>
    </div>

    <div style="display: flex; flex: 1.5; width: 100%; overflow: hidden;">
      <div style="flex: 9; padding: 12px; display: flex; flex-direction: column; overflow-y: auto;">
        <div v-for="(place, index) in artistPlace" :key="index" style="border-bottom: 1px solid #d9d9d9; height: auto; display: flex; padding-bottom: 10px" @click="moveMarker(place, index)">
          <div style="flex-grow: 1; padding: 5px; display: flex; flex-direction: column;">
            <span style="font-size: 20px;">{{place.placeName}}</span>
            <span>{{place.addressName}}</span>
            <span style="font-size: 12px;">{{place.roadAddressName}}</span> <br/>

            <div>
              <span style="margin-top: 20px;">Memo</span>
              <div style="border: 1px solid #d9d9d9; border-radius: 8px; margin-top: 8px; min-height: 40px; display: flex; align-items: center; padding: 5px">{{place.memo}}</div>
            </div>
          </div>
        </div>
      </div>


      <div style="flex: 1; padding: 10px">
        <v-btn @click="addPlace" variant="outlined" color="pink">장소 추가</v-btn>
      </div>
    </div>

    <v-dialog v-model="addPlaceDialog" persistent width="900" height="680"  style="font-family: Dovemayo_wild, sans-serif ">
      <v-card class="pa-3" style="height: 680px; width: 900px; overflow-y: auto">
        <v-card-title class="d-flex text-center justify-center" style="font-size: 32px;">
          <v-row class="align-center text-center">
            <v-col></v-col>
            <v-col>장소 추가하기</v-col>
            <v-col>
              <v-card-actions class="justify-end">
                <v-btn variant="plain" @click="closeAddPlaceDialog">
                  <v-icon size="30">mdi-close</v-icon>
                </v-btn>
              </v-card-actions>
            </v-col>
          </v-row>
        </v-card-title>

        <v-card-actions style="width: 100%; align-items: center; display: flex; justify-content: center; margin-top: 10px">
          <div style="width: 60%;">
            <v-text-field
                :loading="loading"
                v-model="searchKeyword"
                append-inner-icon="mdi-magnify"
                density="comfortable"
                label="Search Place"
                variant="outlined"
                hide-details
                single-line
                @keydown.enter="onClick"
                @click:append-inner="onClick"
            ></v-text-field>
          </div>
        </v-card-actions>

        <v-card-actions style="width: 100%; height: 50%; flex-grow: 1; display: flex; justify-content: center; position: relative;">
          <div id="searchMap" style="z-index: 1; top: 0; left: 0; width: 100%; height: 100%; position: absolute"></div>

          <div style="position: absolute; top: 0; right: 0; z-index: 5; flex: 1; height: 100%; width: 30%; opacity: 0.8; background-color: white; display: flex; flex-direction: column; overflow-y: auto">
            <v-radio-group v-model="selectPlace">
              <div v-for="(place, index) in searchList" :key="index" style="border-bottom: 1px solid #d9d9d9; height: auto; display: flex; flex-direction: column; padding: 8px">
                <v-radio
                    :value="place"
                    :label="place.place_name"
                    density="compact"
                ></v-radio>
                <div style="font-size: 12px">
                  {{place.address_name}} <br/>
                  {{place.road_address_name}} <br/>
                  <span style="color: green">{{place.phone}}</span>
                </div>
              </div>
            </v-radio-group>
          </div>
        </v-card-actions>

        <v-card-actions>
          <div style="display: flex; width: 100%; flex-direction: column">
          <div>이미지 선택</div>

          <div style="margin-top: 10px; width: 100%; height: 300px; display: flex; justify-content: center; align-items: center; text-align: center; border: 1px solid #d9d9d9; border-radius: 12px">
            <div v-if="!previewPlaceImageURL" style="width: 100%" @click="addPlaceImg">
              <v-icon>mdi-camera</v-icon>
              클릭해서 이미지를 추가해주세요
            </div>
            <v-img :src="previewPlaceImageURL" alt="이미지 미리보기" ></v-img>
            <input type="file" style="display: none" accept=".jpg, .jpeg, .png" ref="uploadItemFile" @change="previewPlaceImage" />
          </div>
          </div>
        </v-card-actions>

        <v-card-actions>
          <div style="display: flex; width: 100%; flex-direction: column">
            <div>메모 추가</div>

            <div style="margin-top: 10px; width: 100%; display: flex; justify-content: center; align-items: center; text-align: center; border: 1px solid #d9d9d9; border-radius: 12px">
              <v-textarea
                  rows="4"
                  v-model="memo"
                  density="comfortable"
                  label="memo"
                  variant="outlined"
                  hide-details
                  single-line
              ></v-textarea>
            </div>
          </div>
        </v-card-actions>

        <v-card-actions style="width: 100%;">
          <v-btn @click="savePlace" style="height: 45px; width: 100%" variant="flat" color="blue">저장하기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";
import {mapActions, mapState} from "vuex";

const imageConfig = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Content-Type': 'multipart/form-data'
  }
};

export default defineComponent({
  name: "ArtistMapMarkerPageView",
  data() {
    return {
      map: null,
      placeMarker: [],
      placeOverlay: [],
      addPlaceDialog: false,
      loaded: false,
      loading: false,
      searchMap: null,
      searchKeyword: '',
      searchMarkers: [],
      searchList: [],
      infowindow: null,
      previewPlaceImageURL: null,
      selectPlaceImageFile: null,
      selectPlace: null,
      placeInfo: [
        {
            address_name: '서울 강남구 신사동 514-21',
            place_image: 'artistPlace/카이 (KAI)/dkfma3@naver.com/2024-07-01/images/192fedda-6671-48ee-9328-874c51309a6c.jpeg',
            place_name: '신사 890',
            road_address_name: '서울 강남구 강남대로152길 26',
            x: '127.021554270111',
            y: '37.5175403848646',
            placeUrl: 'https://www.naver.com'
        },
        {
          address_name: '제주도도도도',
          place_image: 'artistPlace/카이 (KAI)/dkfma3@naver.com/2024-07-01/images/192fedda-6671-48ee-9328-874c51309a6c.jpeg',
          place_name: ' 카카오 스페이스닷원',
          road_address_name: '서울 강남구 강남대로152길 26',
          x: '126.570667',
          y: '33.450701',
          placeUrl: 'https://www.daum.net'
        }
      ],
      activeOverlayIndex: null,
      memo: '',
    }
  },
  mounted() {
    let artist = this.$route.params.artist;
    this.fetchArtistPlace({artist})

    this.$nextTick(() => {
      this.initializeMap();
    });
  },
  computed: {
    ...mapState(['artistPlace'])
  },
  methods: {
    ...mapActions(['fetchArtistPlace']),
    initializeMap() {
      let kakao = window.kakao;
      if (typeof kakao === 'undefined' || typeof kakao.maps === 'undefined') {
        console.error('Kakao Maps API is not loaded.');
        return;
      }

      const container = document.getElementById('map');
      const options = {
        center: new kakao.maps.LatLng(37.5175403848646, 127.021554270111),
        level: 3
      };

      this.map = new kakao.maps.Map(container, options);

      this.addPlaceMarker()
    },
    moveMarker(place, index){
      console.log(place)
      let marker = this.placeMarker[index];
      if (marker) {
        let position = marker.getPosition();
        this.map.setCenter(position);
        let overaly = this.placeOverlay[index];
        overaly.setMap(this.map);
      }
    },
    addPlaceMarker(){
      let kakao = window.kakao;

      this.artistPlace.forEach((place, index) => {
        let imageSrc = 'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/marker/marker.png',
            imageSize = new kakao.maps.Size(32, 32),  // 마커 이미지의 크기
            imgOptions = {
              spriteSize: new kakao.maps.Size(32, 38), // 스프라이트 이미지의 크기
              offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
            },
            markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions)


        let marker = new kakao.maps.Marker({
          map: this.map,
          position: new kakao.maps.LatLng(place.lng, place.lat),
          image: markerImage
        });

        let image = `https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/${place.placeImage}`
        console.log(image)

        let content2 = document.createElement('div');
        content2.className = 'place-info-div';
        content2.innerHTML = `
          <div class="place-info-btn"><span class="mdi mdi-close"></span></div>
          <div class="img-div" style="background-image: url('${image}');"></div>
          <div class="place-info">

            <span class="place-name"> <a class="move-page" href="${place.placeUrl}" target='_blank'>${place.placeName}</a></span><br/>
            ${place.addressName} <br/>
            <span class="place-road-name">${place.roadAddressName}</span>
          </div>
        `;


        let overlay = new kakao.maps.CustomOverlay({
          content: content2,
          //map: this.map,
          position: marker.getPosition(),
          //yAnchor: -1
        });

       kakao.maps.event.addListener(marker, 'click', () => {
         this.placeOverlay.forEach((ov, ovIndex) => {
           if(ovIndex !== index){
             ov.setMap(null)
           }
         });

          overlay.setMap(this.map);
          this.activeOverlayIndex = index;
        });

        let closeBtn = content2.querySelector('.place-info-btn span');
        closeBtn.addEventListener('click', () => {
          this.closeOverlay(index);
        });


        //marker.setMap(this.map)

        this.placeMarker.push(marker)
        this.placeOverlay.push(overlay)
      })
    },
    closeOverlay(index){
      this.placeOverlay[index].setMap(null);

      if (this.activeOverlayIndex === index) {
        this.activeOverlayIndex = null;
      }
    },
    initializeSearchMap() {
      let kakao = window.kakao;
      if (typeof kakao === 'undefined' || typeof kakao.maps === 'undefined') {
        console.error('Kakao Maps API is not loaded.');
        return;
      }

      const container = document.getElementById('searchMap');
      const options = {
        center: new kakao.maps.LatLng(37.52361111, 126.8983417),
        level: 3
      };
      this.searchMap = new kakao.maps.Map(container, options);
      this.infowindow = new kakao.maps.InfoWindow({zIndex:1});
    },
    addPlace(){
      this.addPlaceDialog = true;

      this.$nextTick(() => {
        this.initializeSearchMap();
      });
    },
    closeAddPlaceDialog(){
      this.searchKeyword = '';
      this.searchMarkers = [];
      this.searchList = [];
      this.infowindow = null;
      this.previewPlaceImageURL = null;
      this.selectPlaceImageFile = null;
      this.addPlaceDialog = false;
    },
    addPlaceImg(){
      this.$refs.uploadItemFile.click();
    },
    previewPlaceImage(event){
      const file = event.target.files[0];

      if(file) {
        const reader = new FileReader();

        reader.onload = () => {
          this.previewPlaceImageURL = reader.result;
        };

        reader.readAsDataURL(file);

        this.selectPlaceImageFile = file;
      }
    },
    onClick() {
      this.loading = true
      let kakao = window.kakao;
      let ps = new kakao.maps.services.Places();

      if (this.searchKeyword === '') {
        alert('키워드를 입력해주세요!')
      } else{
        ps.keywordSearch(this.searchKeyword, this.placesSearchCB)
      }
    },
    placesSearchCB(data, status){
      let kakao = window.kakao;
      if (status === kakao.maps.services.Status.OK) {
        this.displayPlaces(data);

        this.loading = false;
        this.loaded = true;
      }else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
      } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 결과 중 오류가 발생했습니다.');
      }
    },
    displayPlaces(places){
      let kakao = window.kakao;
      let bounds = new kakao.maps.LatLngBounds();

      this.searchList = [];
      this.searchMarkers = [];

      for ( var i=0; i<places.length; i++ ) {
        let placePosition = new kakao.maps.LatLng(places[i].y, places[i].x)

        let marker = this.addMarker(placePosition);

        bounds.extend(placePosition);

        this.markerEvent(marker, places[i].place_name)

        this.searchList.push(places[i]);
        this.searchMap.setBounds(bounds)
      }
    },
    markerEvent(marker, title){
      let kakao = window.kakao;

      kakao.maps.event.addListener(marker, 'mouseover', () => {
        this.displayInfowindow(marker, title);
      });

      this.searchList.onmouseover =  function () {
        this.displayInfowindow(marker, title);
      };

    },
    displayInfowindow(marker, title) {
      var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

      this.infowindow.setContent(content);
      this.infowindow.open(this.searchMap, marker);
    },
    addMarker(position){
      let kakao = window.kakao;

      let imageSrc = 'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/marker/marker.png',
          imageSize = new kakao.maps.Size(32, 32),  // 마커 이미지의 크기
          imgOptions = {
            spriteSize: new kakao.maps.Size(32, 38), // 스프라이트 이미지의 크기
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
          },
          markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
          marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage
          });

      marker.setMap(this.searchMap); // 지도 위에 마커를 표출합니다
      this.searchMarkers.push(marker);

      return marker;
    },
    savePlace(){
      if(this.previewPlaceImageURL && this.selectPlace != null && this.memo){
        console.log(this.selectPlace)
        let formData = new FormData();
        let placeImage = this.selectPlaceImageFile;
        let Lat = this.selectPlace.x.toString();
        let Lng = this.selectPlace.y.toString();
        let address_name = this.selectPlace.address_name;
        let road_address_name = this.selectPlace.road_address_name;
        let placeName = this.selectPlace.place_name;
        let placeUrl = this.selectPlace.place_url;

        formData.append('placeImage', placeImage)
        formData.append('Lat', Lat)
        formData.append('Lng', Lng)
        formData.append('addressName', address_name)
        formData.append('roadAddressName', road_address_name)
        formData.append('placeName', placeName)
        formData.append('placeUrl', placeUrl)
        formData.append('memo', this.memo)

        let artist = this.$route.params.artist;
        axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/savePlace`, formData, imageConfig)
            .then((res) => {
              console.log(res.data)
              window.location.reload();
            })
            .catch((err) => {
              console.error(err)
            })

      }else {
        alert("장소와 이미지 선택은 필수입니다!")
      }
    }
  },
})
</script>

<style>
.place-info-div {
  position:relative;
  margin: 10px 20px 2px 20px;
  padding: 12px;
  z-index: 1;
  width: 240px;
  height: 300px;
  background-color: #d9d9d9;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}
/*.place-info-div:after {
  content:"";
  position: absolute;
  left: 100px;
  top: 380px;
  border-left: 50px solid transparent;
  border-right: 50px solid transparent;
  border-top: 50px solid #d9d9d9;
}*/
.place-info-btn{
  display: flex;
  justify-content: end;
  color: black;
  width: 100%;
}
.place-img-div {
  flex: 3;
  display: flex;
  justify-content: center;
  align-items: center;
}
.img-div {
  width: 180px;
  height: 180px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
}
.place-info {
  flex: 1;
  color: black;
  font-size: 14px;
  width: 180px;
  margin-top: 4px;
}
.place-name {
  font-size: 20px;
}
.move-page{
  text-decoration-line: none;
  color: black;
}
.place-road-name {
  font-size: 10px;
}
</style>