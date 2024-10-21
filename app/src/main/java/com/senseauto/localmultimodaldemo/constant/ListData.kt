package com.senseauto.localmultimodaldemo.constant

import com.senseauto.localmultimodaldemo.R
import com.senseauto.localmultimodaldemo.entity.CameraScene
import com.senseauto.localmultimodaldemo.entity.DedicatedLanesIdentifyScene
import com.senseauto.localmultimodaldemo.entity.EGirlfriendsScene
import com.senseauto.localmultimodaldemo.entity.GreetingScene
import com.senseauto.localmultimodaldemo.entity.HazardPerceptionScene
import com.senseauto.localmultimodaldemo.entity.HintItem
import com.senseauto.localmultimodaldemo.entity.IntentIdentifyScene
import com.senseauto.localmultimodaldemo.entity.MainSceneItem
import com.senseauto.localmultimodaldemo.entity.POIIdentifyScene
import com.senseauto.localmultimodaldemo.entity.PicScene
import com.senseauto.localmultimodaldemo.entity.PilotIdentifyScene
import com.senseauto.localmultimodaldemo.entity.PoliceGestureIdentifyScene
import com.senseauto.localmultimodaldemo.entity.RoadStatusIdentifyScene
import com.senseauto.localmultimodaldemo.entity.SceneIdentifyScene
import com.senseauto.localmultimodaldemo.entity.SubSceneItem
import com.senseauto.localmultimodaldemo.entity.TrafficIdentifyScene

val ListData = listOf(
    MainSceneItem(
        sceneType = GreetingScene,
        naviIcon = R.drawable.navi_icon_greet_scene,
        titleIcon = R.drawable.title_icon_greet_scene,
        title =  "趣味拟人问候",
        desc = "多模态大模型让车舱具备了极致了的感知和理解能力，可以结合环境和您的心情打招呼，试试和AI对话吧。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = -1,
                hints = listOf(
                    HintItem(
                        hint = "请根据你看到的和我打个招呼吧",
                        prompt = "根据我的穿着、表情和我打个招呼吧，并夸赞一下。"
                    )
                ),
                sceneType = CameraScene
            )
        )
    ),
    MainSceneItem(
        sceneType = SceneIdentifyScene,
        naviIcon = R.drawable.navi_icon_scene_identify,
        titleIcon = R.drawable.title_icon_scene_identify,
        title =  "智能情境识别",
        desc = "多模态大模型能更准确地“看懂”乘客的百变状态，如果看到乘客睡着，AI会及时调控音响和空调。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_scene_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车舱",
                        prompt = "这是一张车舱内的照片，请描述下坐在车舱里的乘客，关注其是否在休息或睡觉。"
                    ),
                    HintItem(
                        hint = "车舱系统现在应当怎么做？",
                        prompt = "你是一个乐于提供帮助的的车载服务助手。观察图片中是否有人在休息或睡觉，并回答可以提供哪些服务使得乘客更舒适。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_scene_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车舱",
                        prompt = "这是一张车舱内的照片，请描述下坐在车舱里的乘客，关注其是否在休息或睡觉。"
                    ),
                    HintItem(
                        hint = "车舱系统现在应当怎么做？",
                        prompt = "你是一个乐于提供帮助的的车载服务助手。观察图片中是否有人在休息或睡觉，并回答可以提供哪些服务使得乘客更舒适。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_scene_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车舱",
                        prompt = "这是一张车舱内的照片，请描述下坐在车舱里的乘客，关注其是否在休息或睡觉。"
                    ),
                    HintItem(
                        hint = "车舱系统现在应当怎么做？",
                        prompt = "你是一个乐于提供帮助的的车载服务助手。观察图片中是否有人在休息或睡觉，并回答可以提供哪些服务使得乘客更舒适。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_scene_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车舱",
                        prompt = "这是一张车舱内的照片，请描述下坐在车舱里的乘客，关注其是否在休息或睡觉。"
                    ),
                    HintItem(
                        hint = "车舱系统现在应当怎么做？",
                        prompt = "你是一个乐于提供帮助的的车载服务助手。观察图片中是否有人在休息或睡觉，并回答可以提供哪些服务使得乘客更舒适。"
                    )
                ),
                sceneType = PicScene
            )
        )
    ),
    MainSceneItem(
        sceneType = HazardPerceptionScene,
        naviIcon = R.drawable.navi_icon_harzard_preception,
        titleIcon = R.drawable.title_icon_harzard_preception,
        title =  "儿童危险感知",
        desc = "多模态大模型能更准确地“看懂”乘客的百变状态，儿童把手伸出窗外时，AI会给出及时的提醒告警。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_harzard_preditc_1,
                hints = listOf(
                    HintItem(
                        hint = "描述车内儿童的状态",
                        prompt = "这是一张车舱内的图片，请描述下图片中的儿童是否存在头或手伸出窗外等危险行为。"
                    ),
                    HintItem(
                        hint = "座舱系统现在应当怎么做?",
                        prompt = "你是一个乐于提供帮助的车载服务助手。这是一张车舱内的图片，请描述图片中的儿童是否存在头手伸出窗外等危险行为。如果有的话，你会提供什么服务来保证乘客的安全？"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_harzard_preditc_2,
                hints = listOf(
                    HintItem(
                        hint = "描述车内儿童的状态",
                        prompt = "这是一张车舱内的图片，请描述下图片中的儿童是否存在头或手伸出窗外等危险行为。"
                    ),
                    HintItem(
                        hint = "座舱系统现在应当怎么做?",
                        prompt = "你是一个乐于提供帮助的车载服务助手。这是一张车舱内的图片，请描述图片中的儿童是否存在头手伸出窗外等危险行为。如果有的话，你会提供什么服务来保证乘客的安全？"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_harzard_preditc_3,
                hints = listOf(
                    HintItem(
                        hint = "描述车内儿童的状态",
                        prompt = "这是一张车舱内的图片，请描述下图片中的儿童是否存在头或手伸出窗外等危险行为。"
                    ),
                    HintItem(
                        hint = "座舱系统现在应当怎么做?",
                        prompt = "你是一个乐于提供帮助的车载服务助手。这是一张车舱内的图片，请描述图片中的儿童是否存在头手伸出窗外等危险行为。如果有的话，你会提供什么服务来保证乘客的安全？"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_harzard_preditc_4,
                hints = listOf(
                    HintItem(
                        hint = "描述车内儿童的状态",
                        prompt = "这是一张车舱内的图片，请描述下图片中的儿童是否存在头或手伸出窗外等危险行为。"
                    ),
                    HintItem(
                        hint = "座舱系统现在应当怎么做?",
                        prompt = "你是一个乐于提供帮助的车载服务助手。这是一张车舱内的图片，请描述图片中的儿童是否存在头手伸出窗外等危险行为。如果有的话，你会提供什么服务来保证乘客的安全？"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_harzard_preditc_5,
                hints = listOf(
                    HintItem(
                        hint = "描述车内儿童的状态",
                        prompt = "这是一张车舱内的图片，请描述下图片中的儿童是否存在头或手伸出窗外等危险行为。"
                    ),
                    HintItem(
                        hint = "座舱系统现在应当怎么做?",
                        prompt = "你是一个乐于提供帮助的车载服务助手。这是一张车舱内的图片，请描述图片中的儿童是否存在头手伸出窗外等危险行为。如果有的话，你会提供什么服务来保证乘客的安全？"
                    )
                ),
                sceneType = PicScene
            )
        )
    ),
    MainSceneItem(
        sceneType = EGirlfriendsScene,
        naviIcon = R.drawable.navi_icon_e_gf,
        titleIcon = R.drawable.title_icon_e_gf,
        title =  "智能电子闺蜜",
        desc = "多模态大模型让车舱具备了极致的感知和理解能力，不知道如何化妆为好的女士们，AI可以为您提建议。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_e_gf_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下副驾的女性乘客",
                        prompt = "这是一张车舱内场景的图片。请简单描述一下坐在副驾的女性。"
                    ),
                    HintItem(
                        hint = "可以为乘客提供什么妆容建议?",
                        prompt = "你是一个乐于提供帮助的车载助手。给一些化妆建议，贴合副驾的女性气质。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_e_gf_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下副驾的女性乘客",
                        prompt = "这是一张车舱内场景的图片。请简单描述一下坐在副驾的女性。"
                    ),
                    HintItem(
                        hint = "可以为乘客提供什么妆容建议?",
                        prompt = "你是一个乐于提供帮助的车载助手。给一些化妆建议，贴合副驾的女性气质。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_e_gf_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下副驾的女性乘客",
                        prompt = "这是一张车舱内场景的图片。请简单描述一下坐在副驾的女性。"
                    ),
                    HintItem(
                        hint = "可以为乘客提供什么妆容建议?",
                        prompt = "你是一个乐于提供帮助的车载助手。给一些化妆建议，贴合副驾的女性气质。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_e_gf_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下副驾的女性乘客",
                        prompt = "这是一张车舱内场景的图片。请简单描述一下坐在副驾的女性。"
                    ),
                    HintItem(
                        hint = "可以为乘客提供什么妆容建议?",
                        prompt = "你是一个乐于提供帮助的车载助手。给一些化妆建议，贴合副驾的女性气质。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_e_gf_5,
                hints = listOf(
                    HintItem(
                        hint = "描述一下副驾的女性乘客",
                        prompt = "这是一张车舱内场景的图片。请简单描述一下坐在副驾的女性。"
                    ),
                    HintItem(
                        hint = "可以为乘客提供什么妆容建议?",
                        prompt = "你是一个乐于提供帮助的车载助手。给一些化妆建议，贴合副驾的女性气质。"
                    )
                ),
                sceneType = PicScene
            )
        )
    ),
    MainSceneItem(
        sceneType = POIIdentifyScene,
        naviIcon = R.drawable.navi_icon_poi_identify,
        titleIcon = R.drawable.title_icon_poi_identify,
        title =  "AI POI识别",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_poi_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "前方像帆船一样的建筑是什么？",
                        prompt = "请介绍一下前方出现的地标建筑"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_poi_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "这个高高的塔是什么塔？",
                        prompt = "请介绍一下前方出现的地标建筑"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_poi_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "前方的建筑是什么",
                        prompt = "请介绍一下前方出现的地标建筑"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_poi_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "前方彩色的像小蛮腰一样的塔是什么建筑",
                        prompt = "请介绍一下前方出现的地标建筑"
                    )
                ),
                sceneType = PicScene
            ), SubSceneItem(
                sceneCover = R.drawable.scene_cover_poi_identify_5,
                hints = listOf(
                    HintItem(
                        hint = "前方的建筑是什么",
                        prompt = "请介绍一下前方出现的地标建筑"
                    )
                ),
                sceneType = PicScene
            )
        )
    ),
    MainSceneItem(
        sceneType = PoliceGestureIdentifyScene,
        naviIcon = R.drawable.navi_icon_police_gesture_identify,
        titleIcon = R.drawable.title_icon_police_gesture,
        title =  "交通手势理解",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_police_gesture_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆前方的场景",
                        prompt = "用八十个字左右描述一下车辆前方的场景不要截断"
                    ),
                    HintItem(
                        hint = "交警的手势是什么意思？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，请观察交警的手势，并给出相应的驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_police_gesture_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆前方的场景",
                        prompt = "用八十个字左右描述一下车辆前方的场景不要截断"
                    ),
                    HintItem(
                        hint = "交警的手势是什么意思？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，请观察交警的手势，并给出相应的驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_police_gesture_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆前方的场景",
                        prompt = "用八十个字左右描述一下车辆前方的场景不要截断"
                    ),
                    HintItem(
                        hint = "交警的手势是什么意思？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，请观察交警的手势，并给出相应的驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_police_gesture_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆前方的场景",
                        prompt = "用八十个字左右描述一下车辆前方的场景不要截断"
                    ),
                    HintItem(
                        hint = "交警的手势是什么意思？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，请观察交警的手势，并给出相应的驾驶建议。"
                    )
                ),
                sceneType = PicScene
            )
        ),
    ),
    MainSceneItem(
        sceneType = TrafficIdentifyScene,
        naviIcon = R.drawable.navi_icon_traffic_identify,
        titleIcon = R.drawable.title_icon_traffic_identify,
        title =  "行车情境感知",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_traffic_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_traffic_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_traffic_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_traffic_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_traffic_identify_5,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            )
        ),
    ),
    MainSceneItem(
        sceneType = RoadStatusIdentifyScene,
        naviIcon = R.drawable.navi_icon_road_status,
        titleIcon = R.drawable.title_icon_road_status,
        title =  "特殊路况识别",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_road_status_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆前方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下车场前方的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么做？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，请观察图中路况，给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_road_status_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_road_status_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_road_status_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_road_status_5,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "车辆现在应当怎么行驶？",
                        prompt = "你是一位非常有帮助的智能驾驶助手。请识别后方场景的车辆类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            )
        ),
    ),
    MainSceneItem(
        sceneType = PilotIdentifyScene,
        naviIcon = R.drawable.navi_icon_pilot_identify,
        titleIcon = R.drawable.title_icon_pilot_identify,
        title =  "泊车安全感知",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_pilot_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "如果车辆正在倒车应该怎么办？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，在倒车过程中，车辆后方出现图中障碍物，请描述图中场景，包括障碍物类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_pilot_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "如果车辆正在倒车应该怎么办？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，在倒车过程中，车辆后方出现图中障碍物，请描述图中场景，包括障碍物类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_pilot_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "如果车辆正在倒车应该怎么办？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，在倒车过程中，车辆后方出现图中障碍物，请描述图中场景，包括障碍物类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_pilot_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "描述一下车辆后方的场景",
                        prompt = "你是一位非常有帮助的智能助手，请描述一下图中的场景。"
                    ),
                    HintItem(
                        hint = "如果车辆正在倒车应该怎么办？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，在倒车过程中，车辆后方出现图中障碍物，请描述图中场景，包括障碍物类型，并给出驾驶建议。"
                    )
                ),
                sceneType = PicScene
            )
        ),
    ),
    MainSceneItem(
        sceneType = DedicatedLanesIdentifyScene,
        naviIcon = R.drawable.navi_icon_dedicated_lanes_identify,
        titleIcon = R.drawable.titlle_icon_dedicated_lanes_identify,
        title =  "专用车道识别",
        desc = "多模态大模型能更准确地“看懂”舱外的复杂场景，引导智能汽车更聪明地行驶。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_dedicated_lanes_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "右边这条黄色的虚线道路我可以行驶么？",
                        prompt = "你是一位非常有帮助的智能驾驶助手，已知黄色虚线车道是公交车道，请问我可以驾驶小客车在图中黄色虚线车道上行驶吗？为什么？"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_dedicated_lanes_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "这条路上限速多少",
                        prompt = "你是一位非常有帮助的通知驾驶助手，请问当前道路限速多少？"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_dedicated_lanes_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "右边这条黄色的虚线道路我可以行驶么",
                        prompt = "你是一位非常有帮助的智能驾驶助手，已知黄色虚线车道是公交车道，请问我可以驾驶小客车在图中黄色虚线车道上行驶吗？为什么？"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_dedicated_lanes_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "这条路上限速多少",
                        prompt = "你是一位非常有帮助的通知驾驶助手，请问当前道路限速多少？"
                    )
                ),
                sceneType = PicScene
            )
        ),
    ),
    MainSceneItem(
        sceneType = IntentIdentifyScene,
        naviIcon = R.drawable.navi_icon_intent_identify,
        titleIcon = R.drawable.title_icon_intent_identify,
        title =  "智能意图理解",
        desc = "多模态大模型结合POI、音乐、电影、长短视频等数据，理解用户意图并做智能推荐。",
        scenes = listOf(
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_intent_identify_1,
                hints = listOf(
                    HintItem(
                        hint = "根据当前场景推荐一些歌曲",
                        prompt = "你是一位非常有同理心的心情小助手，我正在驾驶汽车，这张图片是目前行驶的路况，使用中文描述一下我现在的心情，不要回复英文。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_intent_identify_2,
                hints = listOf(
                    HintItem(
                        hint = "根据当前场景推荐一些歌曲",
                        prompt = "你是一位非常有同理心的心情小助手，我正在驾驶汽车，这张图片是目前行驶的路况，使用中文描述一下我现在的心情，不要回复英文。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_intent_identify_3,
                hints = listOf(
                    HintItem(
                        hint = "根据当前场景推荐一些歌曲",
                        prompt = "你是一位非常有同理心的心情小助手，我正在驾驶汽车，这张图片是目前行驶的路况，使用中文描述一下我现在的心情，不要回复英文。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_intent_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "根据当前场景推荐一些歌曲",
                        prompt = "你是一位非常有同理心的心情小助手，我正在驾驶汽车，这张图片是目前行驶的路况，使用中文描述一下我现在的心情，不要回复英文。"
                    )
                ),
                sceneType = PicScene
            ),
            SubSceneItem(
                sceneCover = R.drawable.scene_cover_intent_identify_4,
                hints = listOf(
                    HintItem(
                        hint = "生成一张带有我头像的电影海报",
                        prompt = "你是一位非常有同理心的心情小助手，这张图片是一张电影海报，请用中文简单描述一下这个电影，不超过50个汉字"
                    )
                ),
                sceneType = CameraScene
            )
        ),
    )
)