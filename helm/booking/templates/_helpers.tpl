{{- define "booking.name" -}}
{{- default .Chart.Name .Values.nameOverride -}}
{{- end -}}

{{- define "booking.fullname" -}}
{{ printf "%s-%s" (include "booking.name" .) .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "booking.labels" -}}
app: {{ include "booking.name" . }}
chart: "{{ .Chart.Name }}-{{ .Chart.Version }}"
release: {{ .Release.Name }}
heritage: {{ .Release.Service }}
{{- end -}}

{{- define "booking.serviceAccountName" -}}
{{ include "booking.fullname" . }}-sa
{{- end -}}
