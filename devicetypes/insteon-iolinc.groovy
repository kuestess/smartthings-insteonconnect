/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Insteon Switch
 *
 *	Author: hypermoose
 *	Date: 2016-06-19
 *
 *  Updated by kuestess
 *  Date: 07/27/2017
 */
metadata {
	definition (name: "Insteon IOLinc", namespace: "kuestess", author: "kuestess") {
		capability "Actuator"
		capability "Switch"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
        capability "Door Control"
        capability "Garage Door Control"
		capability "Contact Sensor"

	}

tiles {
		standardTile("toggle", "device.door", width: 2, height: 2) {
			state("closed", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", nextState:"opening")
			state("open", label:'${name}', action:"door control.close", icon:"st.doors.garage.garage-open", backgroundColor:"#e86d13", nextState:"closing")
			state("opening", label:'${name}', icon:"st.doors.garage.garage-closed", backgroundColor:"#e86d13")
			state("closing", label:'${name}', icon:"st.doors.garage.garage-open", backgroundColor:"#00A0DC")
			
		}
		standardTile("open", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'open', action:"door control.open", icon:"st.doors.garage.garage-opening"
		}
		standardTile("close", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'close', action:"door control.close", icon:"st.doors.garage.garage-closing"
		}
		standardTile("refresh", "device.door", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

		main "toggle"
		details(["toggle", "open", "close","refresh"])
	}
}


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

void poll() {
	log.debug "Executing 'poll' using parent SmartApp"
	//parent.pollChild()
    parent.pollChildren(this)
}

def generateEvent(Map results) {
	log.debug "generateEvent: parsing data $results"
	if(results) {
    	def level = results.level
        	if(level == 100){
        		sendEvent(name: "door", value: "closed")
        		sendEvent(name: "contact", value: "closed")
            }
            
            if(level == 0){
        		sendEvent(name: "door", value: "open")
        		sendEvent(name: "contact", value: "open")
            }
    }
    
    return null
}

def open() {
	on()
    sendEvent(name: "door", value: "opening")
    runIn(15, refresh)
}

def close() {
    on()
    sendEvent(name: "door", value: "closing")
	runIn(15, refresh)
}

def finishOpening() {
    sendEvent(name: "door", value: "open")
    sendEvent(name: "contact", value: "open")
}

def finishClosing() {
    sendEvent(name: "door", value: "closed")
    sendEvent(name: "contact", value: "closed")
}

def on() {
	log.debug "Sending on"

	if (!parent.switchOn(this, device.deviceNetworkId)) {
		log.debug "Error turning switch on"
	} else {
    	sendEvent(name: "switch", value: "on")
    }
}

def off() {
	log.debug "Sending off"

	if (!parent.switchOff(this, device.deviceNetworkId)) {
		log.debug "Error turning switch off"
	} else {
    	sendEvent(name: "switch", value: "off")
    }
}

def refresh() {
    log.debug "Refreshing.."
    poll()
}